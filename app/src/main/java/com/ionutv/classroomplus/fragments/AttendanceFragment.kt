package com.ionutv.classroomplus.fragments

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentAttendanceBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.activities.MainActivity
import com.ionutv.classroomplus.adapters.ActiveAttendancesRecyclerViewAdapter
import com.ionutv.classroomplus.dialogs.AttendanceScanningDialogFragment
import com.ionutv.classroomplus.factories.AttendanceViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.viewmodels.AttendanceViewModel


class AttendanceFragment : BaseFragment(),
    ActiveAttendancesRecyclerViewAdapter.IOnAttendanceItemClickListener, AttendanceScanningDialogFragment.OnCompleteListener {


    private lateinit var binding: FragmentAttendanceBinding
    private val viewModel: AttendanceViewModel by viewModels {
        AttendanceViewModelFactory(
            App.appContainer.classRoomRepository
        )
    }
    private lateinit var bleAdapter: BluetoothAdapter
    private lateinit var adapter: ActiveAttendancesRecyclerViewAdapter
    private var selectedAttendance :Attendance? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Failed to turn on", Toast.LENGTH_LONG).show()
            }
        }

    private val onAttendanceList = Observer<List<Attendance>> {
            adapter.setItems(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleAdapter =
            (context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        adapter = ActiveAttendancesRecyclerViewAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAttendanceBinding.inflate(inflater)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        binding.rvAttendances.adapter = adapter

        return binding.root
    }

    override fun onScanningComplete() {
        Toast.makeText(context,"Scan complete", Toast.LENGTH_SHORT).show()
        selectedAttendance?.let { viewModel.registerAttendance(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_classroom -> {
                (requireActivity() as MainActivity).refreshClassroom()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttendanceClicked(item: Attendance) {
        val mActivity = activity as MainActivity
        if (mActivity.isLocationPermissionGranted) {
            if (bleAdapter.isEnabled) {
                childFragmentManager.let {
                    val bundle = Bundle()
                    selectedAttendance = item
                    bundle.putString(Constants.ARG_SELECTED_BLE_ID, item.bleId)
                    bundle.putString(Constants.ARG_SELECTED_DEVICE_NAME, item.bleName)
                    bundle.putString(Constants.ARG_SELECTED_CLASS_NAME, item.courseName)
                    AttendanceScanningDialogFragment.newInstance(bundle).apply {
                        show(it, Constants.DIALOG_FRAGMENT_SCANNING_TAG)
                    }
                }
            } else {
                promptEnableBluetooth()
            }
        }

    }

    private fun promptEnableBluetooth() {
        if (!bleAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableIntent)
        }
    }

    override fun connectViewModelEvents() {
        viewModel.currentAttendances.observe(viewLifecycleOwner, onAttendanceList)
    }

    override fun disconnectViewModelEvents() {
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    override fun onPause() {
        super.onPause()
        disconnectViewModelEvents()
    }
}