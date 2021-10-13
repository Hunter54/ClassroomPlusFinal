package com.ionutv.classroomplus.dialogs

import android.animation.Animator
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.DialogScanningBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.activities.MainActivity
import com.ionutv.classroomplus.services.BLEScanService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AttendanceScanningDialogFragment : BottomSheetDialogFragment(),
    BLEScanService.BLEScanCallbacks {

    interface OnCompleteListener {
        fun onScanningComplete()
    }

    private lateinit var binding: DialogScanningBinding
    private lateinit var mListener: OnCompleteListener
    private var attendanceDeviceName: String = ""
    private var bleAdvertiseId: String = ""
    private var attendanceClassName: String = ""
    private lateinit var bleScanService: BLEScanService
    private var timeoutJob: Job? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Failed to turn on", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attendanceDeviceName =
            arguments?.takeIf { it.containsKey(Constants.ARG_SELECTED_DEVICE_NAME) }?.run {
                getString(Constants.ARG_SELECTED_DEVICE_NAME)
            } ?: ""
        bleAdvertiseId = arguments?.takeIf { it.containsKey(Constants.ARG_SELECTED_BLE_ID) }?.run {
            getString(Constants.ARG_SELECTED_BLE_ID)
        } ?: ""
        attendanceClassName = arguments?.takeIf { it.containsKey(Constants.ARG_SELECTED_CLASS_NAME) }?.run {
            getString(Constants.ARG_SELECTED_CLASS_NAME)
        } ?: ""
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bleManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        bleScanService = BLEScanService(
            bleManager.adapter,
            this as BLEScanService.BLEScanCallbacks,
            attendanceDeviceName
        )

        binding = DialogScanningBinding.inflate(inflater)
        binding.tvDeviceName.text = getString(R.string.scanning_for_message, attendanceClassName)
        binding.btnScanning.setOnClickListener(scanClickListener)
        binding.btnSubmit.setOnClickListener {
            mListener.onScanningComplete()
            dismiss()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bleScanService.startBLEScan()
        setProgressAnimation()
    }

    private fun setTimeout(): Job =
        viewLifecycleOwner.lifecycleScope.launch {
            delay(60000)
            bleScanService.stopScan()
            setFailAnimation()
        }

    private fun setProgressAnimation() {
        timeoutJob = setTimeout()
        binding.animationViewProgress.speed = 1.1F
        binding.animationViewProgress.setMinAndMaxFrame(0, 119)
        binding.animationViewProgress.playAnimation()
        binding.animationViewProgress.repeatCount = LottieDrawable.INFINITE
        binding.animationViewProgress.repeatMode = LottieDrawable.RESTART
    }

    private fun setSuccessAnimation() {
        timeoutJob?.cancel()
        binding.animationViewProgress.speed = 1.25F
        binding.animationViewProgress.addAnimatorListener(animationSuccessListener)
    }

    private fun setFailAnimation() {
        binding.animationViewProgress.speed = 1.25F
        binding.animationViewProgress.addAnimatorListener(animationFailListener)
    }

    private val animationSuccessListener = object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator?) {
            binding.animationViewProgress.removeAllAnimatorListeners()
            binding.tvDeviceName.text = getString(R.string.device_found_message)
            binding.btnScanning.visibility = View.INVISIBLE
            binding.btnSubmit.visibility = View.VISIBLE
        }

        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {
            binding.animationViewProgress.setMinAndMaxFrame(238, 380)
            binding.animationViewProgress.repeatCount = 0
            binding.animationViewProgress.playAnimation()
        }
    }

    private val animationFailListener = object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator?) {
            binding.animationViewProgress.removeAllAnimatorListeners()
            binding.tvDeviceName.text = getString(R.string.try_again_message)
        }

        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {
            binding.animationViewProgress.setMinAndMaxFrame(657, 825)
            binding.animationViewProgress.repeatCount = 0
            binding.animationViewProgress.playAnimation()
        }
    }

    private val scanClickListener = View.OnClickListener {
        val mActivity = activity as MainActivity
        if (mActivity.isLocationPermissionGranted) {
            if (bleScanService.isBluetoothEnabled()) {
                if (!bleScanService.isScanning) {
                    bleScanService.startBLEScan()
                    binding.btnScanning.text = getString(R.string.stop_scan_message)
                    setProgressAnimation()
                } else {
                    bleScanService.stopScan()
                    binding.btnScanning.text = getString(R.string.start_scan_message)
                    setFailAnimation()
                }
            } else {
                promptEnableBluetooth()
            }
        }
    }

    private fun promptEnableBluetooth() {
        if (!bleScanService.isBluetoothEnabled()) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableIntent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = parentFragment as OnCompleteListener
        } catch (error: ClassCastException) {
            throw ClassCastException(parentFragment.toString() + " must implement OnCompleteListener")
        }
    }

    override val scanCallback: ScanCallback
        get() = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val data = bleScanService.decodeScanResult(result)
                with(result.device) {
                    if (data == bleAdvertiseId) {
                        setSuccessAnimation()
                        bleScanService.stopScan()
                    }
                }
            }
        }

    override fun onPause() {
        super.onPause()

    }

    companion object {
        fun newInstance(bundle: Bundle): AttendanceScanningDialogFragment {
            val fragment = AttendanceScanningDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}