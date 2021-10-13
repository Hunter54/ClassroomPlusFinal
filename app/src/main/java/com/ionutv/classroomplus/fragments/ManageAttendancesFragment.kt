package com.ionutv.classroomplus.fragments

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentManageAttendancesBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.activities.MainActivity
import com.ionutv.classroomplus.adapters.ActiveAttendancesRecyclerViewAdapter
import com.ionutv.classroomplus.adapters.AllClassesAttendancesRecyclerViewAdapter
import com.ionutv.classroomplus.dialogs.DialogAttendanceFragment
import com.ionutv.classroomplus.factories.ManageAttendancesViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.services.BLEAdvertiserService
import com.ionutv.classroomplus.viewmodels.ManageAttendancesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageAttendancesFragment : BaseFragment(), BLEAdvertiserService.BLEAdvertiseCallbacks,
    DialogAttendanceFragment.OnCompleteListener,
    ActiveAttendancesRecyclerViewAdapter.IOnAttendanceItemClickListener,
    AllClassesAttendancesRecyclerViewAdapter.IOnClassItemClickListener {

    private lateinit var binding: FragmentManageAttendancesBinding
    private val viewModel: ManageAttendancesViewModel by viewModels {
        ManageAttendancesViewModelFactory(
            App.appContainer.classRoomRepository
        )
    }
    private lateinit var bleAdvertiserService: BLEAdvertiserService
    private lateinit var activeAttendanceAdapter: ActiveAttendancesRecyclerViewAdapter
    private lateinit var classesAdapter: AllClassesAttendancesRecyclerViewAdapter
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                showUnableToTurnOnBluetoothDialog()
            }
        }
    private var timeoutJob: Job? = null
    private var timeoutTime: Long = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdvertiserService = BLEAdvertiserService(
            bluetoothManager.adapter,
            this as BLEAdvertiserService.BLEAdvertiseCallbacks
        )
        activeAttendanceAdapter =
            ActiveAttendancesRecyclerViewAdapter(this as ActiveAttendancesRecyclerViewAdapter.IOnAttendanceItemClickListener)
        classesAdapter =
            AllClassesAttendancesRecyclerViewAdapter(this as AllClassesAttendancesRecyclerViewAdapter.IOnClassItemClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageAttendancesBinding.inflate(inflater)
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        binding.rvManageAttendances.adapter = classesAdapter
        binding.rvActiveAttendances.adapter = activeAttendanceAdapter
        binding.pbAdvertising.hide()
        binding.fabAttendances.setOnClickListener {
            if (activeAttendanceAdapter.items.isEmpty()) {
                childFragmentManager.let {
                    DialogAttendanceFragment.newInstance(Bundle()).apply {
                        show(it, Constants.DIALOG_FRAGMENT_SAVE_TAG)
                    }
                }
            } else {
                showAlertDialog()
            }
        }
        return binding.root
    }

    private fun promptEnableBluetooth() {
        if (!bleAdvertiserService.bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableIntent)
        }
    }

    private val onAdvertiseClicked = Observer<Void> {
        if (bleAdvertiserService.isAdvertising) {
            disableBleAdvertising()
        } else {
            if (bleAdvertiserService.bluetoothAdapter.isEnabled) {
                enableBleAdvertising()
            } else {
                promptEnableBluetooth()
            }
        }
    }

    private val onActiveAttendancesChanged = Observer<List<Attendance>> {
        activeAttendanceAdapter.setItems(it)
    }

    private val onClassesChanged = Observer<List<CourseEntry>> {
        classesAdapter.setItems(it)
    }

    private val onAttendanceDeleted = Observer<String?> {
        it?.let {
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            activeAttendanceAdapter.removeClass(it)
            disableBleAdvertising()
        } ?: run {
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).apply {
                titleText = "Error Deleting"
                contentText =
                    "An error occured while trying to delete this attendance. Please check you internet connection"
                setCancelable(true)
                show()
            }
        }
    }

    override fun onAttendanceClicked(item: Attendance) {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Do you want to delete this attendance?")
            .setContentText("This can not be undone")
            .setConfirmButton("Yes,delete!") {
                viewModel.deleteActiveAttendance(item.courseId)
                it.dismiss()
            }
            .setCancelButton("Cancel") {
                it.dismiss()
            }
            .show()
    }

    private fun setupTimeout(): Job =
        viewLifecycleOwner.lifecycleScope.launch {
            delay(timeoutTime)
            activeAttendanceAdapter.items.firstOrNull()?.let {
                viewModel.deleteActiveAttendance(it.courseId)
            }
            disableBleAdvertising()
        }

    override fun onAttendanceLongPressed(item: Attendance): Boolean {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Do you want to delete this attendance?")
            .setContentText("This can not be undone")
            .setConfirmButton("Yes,delete!") {
                viewModel.deleteActiveAttendance(item.courseId)
                it.dismiss()
            }
            .setCancelButton("Cancel") {
                it.dismiss()
            }
            .show()
        return true
    }

    override fun onClassClicked(item: CourseEntry) {
        val navigationController = findNavController()
        val direction =
            ManageAttendancesFragmentDirections.actionManageAttendancesFragmentToClassAttendancesOverviewFragment(
                item.id,
                item.name
            )
        navigationController.navigate(direction)
    }

    private fun disableBleAdvertising() {
        timeoutJob?.cancel()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        bleAdvertiserService.stopAdvertising()
        binding.pbAdvertising.hide()
        binding.tvAdvertising.visibility = View.INVISIBLE
        Toast.makeText(context, "Stopping advertising", Toast.LENGTH_LONG).show()
    }

    private fun enableBleAdvertising() {
        if (!bleAdvertiserService.bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
            return
        }
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        timeoutJob = setupTimeout()
        bleAdvertiserService.startBLEAdvertiser()
        binding.pbAdvertising.show()
        binding.tvAdvertising.visibility = View.VISIBLE
        Toast.makeText(context, "Starting advertising", Toast.LENGTH_LONG).show()
    }

    override fun onComplete(attendance: Attendance) {
        timeoutTime = attendance.duration * Constants.MILIS_IN_MINUTES
        bleAdvertiserService.generateNewAdvertiseData()
        attendance.bleId = bleAdvertiserService.advertiseData
        attendance.bleName = bleAdvertiserService.bluetoothAdapter.name
        viewModel.uploadAttendance(attendance)
        enableBleAdvertising()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.onActiveAttendancesChanged.observe(viewLifecycleOwner, onActiveAttendancesChanged)
        viewModel.onAdvertiseClicked.observe(viewLifecycleOwner, onAdvertiseClicked)
        viewModel.onCoursesWhereTeacher.observe(viewLifecycleOwner, onClassesChanged)
        viewModel.onAttendanceDelete.observe(viewLifecycleOwner, onAttendanceDeleted)
    }

    override fun connectViewModelEvents() {
        viewModel.getActiveAttendances()
    }

    override fun disconnectViewModelEvents() {
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

    private fun showUnableToTurnOnBluetoothDialog() {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Unable to start Advertising")
            .setContentText("Please enable bluetooth to continue")
            .setConfirmButton("Ok") {
                it.dismiss()
                promptEnableBluetooth()
            }
            .setCancelButton("Cancel") {
                it.dismiss()
            }
            .show()
    }


    private fun showAlertDialog() {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Can not create new attendance")
            .setContentText("There is already an attendance request in progress")
            .setConfirmButton("Ok") {
                it.dismiss()
            }
            .show()
    }

    override val advertiseCallback: AdvertiseCallback
        get() = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                disableBleAdvertising()
                if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Can not advertise")
                        .setContentText("Phone name too long, please choose a smaller bluetooth name for your phone!")
                        .setConfirmButton("Ok") {
                            it.dismiss()
                        }
                        .show()
                }
                Toast.makeText(
                    context,
                    "BLE Advertising onStartFailure: $errorCode",
                    Toast.LENGTH_LONG
                ).show()
                super.onStartFailure(errorCode)
            }
        }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    override fun onPause() {
        super.onPause()
        disconnectViewModelEvents()
        disableBleAdvertising()
    }
}