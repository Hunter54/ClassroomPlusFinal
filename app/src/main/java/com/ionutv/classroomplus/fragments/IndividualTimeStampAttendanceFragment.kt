package com.ionutv.classroomplus.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentIndividualTimestampAttendanceBinding
import com.ionutv.classroomplus.adapters.IndividualClassAttendanceListRecyclerViewAdapter
import com.ionutv.classroomplus.viewmodels.AttendanceOverviewViewModel

class IndividualTimeStampAttendanceFragment : BaseFragment() {

    private lateinit var binding: FragmentIndividualTimestampAttendanceBinding
    private val viewModel: AttendanceOverviewViewModel by navGraphViewModels(R.id.classAttendanceGraph)
    private val adapter =
        IndividualClassAttendanceListRecyclerViewAdapter()
    private val fragmentArgs by navArgs<IndividualTimeStampAttendanceFragmentArgs>()
    private val progressDialog by lazy {
        SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndividualTimestampAttendanceBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvAttendees.adapter = adapter
        binding.executePendingBindings()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.onAttendancesListChanged.value?.get(fragmentArgs.attendanceTimeStamp)?.let {
            adapter.setItems(it)
        }
        viewModel.onSuccessUpload.observe(viewLifecycleOwner, onSuccessUpload)
        viewModel.onIsBusy.observe(viewLifecycleOwner, onIsBusy)
        viewModel.onSpreadSheetError.observe(viewLifecycleOwner, onSpreadsheetError)
        viewModel.onSheetAlreadyExists.observe(viewLifecycleOwner, onSheetAlreadyExists)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.share_menu, menu)
    }

    private val onIsBusy = Observer<Boolean> {
        if (it) {
            progressDialog.apply {
                titleText = "Uploading to Google SpreadSheet"
                contentText = "Check your internet connection"
                show()
            }
        } else {
            progressDialog.dismiss()
        }
    }

    private val onSuccessUpload = Observer<Void> {
        Snackbar.make(binding.root, "Successful uploaded ", 10000)
            .setAction("Copy link") {
                viewModel.classroomSpreadsheet?.let {
                    val clipboard =
                        context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("attendance sheet link", it.spreadsheetUrl)
                    clipboard.setPrimaryClip(clip)
                } ?: run {
                    Toast.makeText(context, "There was an error", Toast.LENGTH_LONG)
                }
            }
            .show()
    }

    private val onSheetAlreadyExists = Observer<Int> { sheetId ->
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Failed to create sheet")
            .setContentText("Looks like this sheet already exists, do you want to overwrite it?")
            .setConfirmButton("Ok") {
                viewModel.classroomSpreadsheet?.let { spreadSheet ->
                    viewModel.updateSheet(
                        spreadSheet,
                        sheetId,
                        fragmentArgs.attendanceTimeStamp
                    )
                }
                it.dismiss()
            }
            .setCancelButton("Cancel") {
                it.dismissWithAnimation()
            }
            .show()
    }

    private val onSpreadsheetError = Observer<GoogleJsonResponseException> { error ->
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share_attendance -> {
                viewModel.classroomSpreadsheet?.let {
                    viewModel.addSheetAndUpdateSpreadSheet(
                        it,
                        fragmentArgs.attendanceTimeStamp
                    )
                } ?: run {
                    Toast.makeText(
                        context,
                        "Please wait while fetching the spreadsheet",
                        Toast.LENGTH_LONG
                    ).show()
                }
                true
            }
            else -> false
        }
    }

    override fun connectViewModelEvents() {
        TODO("Not yet implemented")
    }

    override fun disconnectViewModelEvents() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}