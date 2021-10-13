package com.ionutv.classroomplus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentAttendanceOverviewBinding
import com.ionutv.classroomplus.adapters.SelectedClassAttendancesRecyclerViewAdapter
import com.ionutv.classroomplus.factories.AttendanceOverviewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.viewmodels.AttendanceOverviewViewModel

class SelectedClassAttendancesOverviewFragment : BaseFragment(),
    SelectedClassAttendancesRecyclerViewAdapter.IOnTimeItemClickListener {

    private lateinit var binding: FragmentAttendanceOverviewBinding
    private val viewModel: AttendanceOverviewViewModel by navGraphViewModels(R.id.classAttendanceGraph) {
        AttendanceOverviewModelFactory(App.appContainer.sheetsRepository)
    }
    private val adapter =
        SelectedClassAttendancesRecyclerViewAdapter(this as SelectedClassAttendancesRecyclerViewAdapter.IOnTimeItemClickListener)
    private val fragmentArgs by navArgs<SelectedClassAttendancesOverviewFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAttendanceForClassroomId(fragmentArgs.classroomId)
        viewModel.getSpreadSheetId(fragmentArgs.classroomId, fragmentArgs.classroomName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceOverviewBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        binding.rvDateAndTime.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAttendanceForClassroomId(fragmentArgs.classroomId)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.onTimeStampList.observe(viewLifecycleOwner, onTimeStampListChanged)
        viewModel.wasDeleted.observe(viewLifecycleOwner, onDeleted)
        viewModel.onTimeStampList.value?.let {
            adapter.setItems(it)
        }
        viewModel.onSpreadSheetError.observe(viewLifecycleOwner, onSpreadSheetError)
    }

    override fun onTimeLongPressed(item: String) {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Do you want to delete this?")
            .setContentText("This operation can not be reverted")
            .setConfirmButton("Delete") {
                it.dismissWithAnimation()
                viewModel.deleteAttendanceTimeStamp(fragmentArgs.classroomId, item)
            }
            .setCancelButton("Cancel") {
                it.dismissWithAnimation()
            }
            .show()
    }

    override fun onTimeClicked(item: String) {
        val navigationController = findNavController()
        val direction =
            SelectedClassAttendancesOverviewFragmentDirections.actionSelectedClassAttendancesOverviewFragmentToIndividualClassAttendanceFragment(
                item
            )
        navigationController.navigate(direction)
    }

    private val onTimeStampListChanged = Observer<List<String>> {
        if (binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = false
        }
        adapter.setItems(it)
    }

    private val onSpreadSheetError = Observer<GoogleJsonResponseException> { error ->
        if (error.statusCode == 404) {
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Failed retrieve sheet")
                .setContentText("Looks like the previous sheet was deleted or other teacher overridden it! \nDo you want to recreate it, this will delete previous sheetLink from database?")
                .setConfirmButton("Ok") {
                    viewModel.createSpreadSheet(
                        fragmentArgs.classroomName,
                        fragmentArgs.classroomId
                    )
                    it.dismissWithAnimation()
                }
                .setCancelButton("Cancel") {
                    it.dismissWithAnimation()
                }
                .show()
        } else {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }
    }

    private val onDeleted = Observer<Boolean> { wasDeleted ->
        if (wasDeleted) {
            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show()
            viewModel.getAttendanceForClassroomId(fragmentArgs.classroomId)
            binding.swipeRefresh.isRefreshing = true
        } else {
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Failed to delete")
                .setContentText("Check your internet connection")
                .setConfirmButton("Ok") {
                    it.dismissWithAnimation()
                }
                .show()
        }
    }

    override fun connectViewModelEvents() {

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