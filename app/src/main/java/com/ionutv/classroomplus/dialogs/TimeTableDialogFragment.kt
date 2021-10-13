package com.ionutv.classroomplus.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.DialogTimetableBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.factories.TimeTableDialogViewModelFactory
import com.ionutv.classroomplus.fragments.ClassesFragment
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.viewmodels.TimeTableDialogViewModel

class TimeTableDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogTimetableBinding
    private lateinit var mListener: OnCompleteListener
    private var classesFragment: ClassesFragment? = null
    private val viewModel: TimeTableDialogViewModel by viewModels {
        TimeTableDialogViewModelFactory(
            App.appContainer.classRoomRepository,
            App.appContainer.timeTableRepository
        )
    }
    private var timeTableEntry: TimeTableEntry? = null
    private var selectedId: Int = 0

    interface OnCompleteListener {
        fun onSavePress(timeTable: TimeTableEntry)
    }

    private val onCancelTapped = Observer<Void> {
        this.dismiss()
    }

    private val onError = Observer<Int> {
        Snackbar.make(binding.root, getString(it), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.ok_key)) { }
            .show()
    }

    private val onCoursesListChanged = Observer<List<CourseEntry>> { list ->
        val courseNameArray = Array(list.size) { list[it].name }
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            courseNameArray
        )
        binding.autoCompleteNameTextInput.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedId = arguments?.takeIf { it.containsKey(Constants.ARG_SELECTED_ID) }?.run {
            getInt(Constants.ARG_SELECTED_ID)
        } ?: 0
        if (selectedId != 0) {
            viewModel.setupSelectedItem(selectedId)
        }
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTimetableBinding.inflate(inflater)

        val modalBottomSheetBehavior = (this.dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.peekHeight = 1000
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setupDialog()

        return binding.root
    }

    private fun setupDialog() {
        binding.saveButton.setOnClickListener {
            if (viewModel.validateData()) {
                val timeTableEntry = viewModel.createTimeTableEntry()
                this.mListener.onSavePress(timeTableEntry)
                this.dismiss()
            }

        }
        binding.classChoiceChipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onCurrentClassTypeChanged(checkedId)
        }
        binding.dayChoiceChipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onCurrentClassDayChanged(checkedId)
        }
        binding.weekChoiceChipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onCurrentClassWeekChanged(checkedId)
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

    companion object {
        fun newInstance(bundle: Bundle): TimeTableDialogFragment {
            val fragment = TimeTableDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun connectViewModelEvents() {
        viewModel.onCancelTapped.observe(this, onCancelTapped)
        viewModel.onError.observe(this, onError)
        viewModel.onClassroomCourses.observe(this, onCoursesListChanged)
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }
}