package com.ionutv.classroomplus.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.DialogAttendanceBinding
import com.ionutv.classroomplus.factories.ManageAttendanceDialogViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.utils.ClassValidator
import com.ionutv.classroomplus.viewmodels.ManageAttendanceDialogViewModel

class DialogAttendanceFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAttendanceBinding
    private lateinit var mListener: OnCompleteListener
    private val viewModel: ManageAttendanceDialogViewModel by viewModels {
        ManageAttendanceDialogViewModelFactory(
            App.appContainer.classRoomRepository
        )
    }

    interface OnCompleteListener {
        fun onComplete(attendance : Attendance)
    }

    private val onStartTapped = Observer<Void> {
        binding.autoCompleteNameTextInput.performValidation()
        if (viewModel.validateData()) {
            val attendance = viewModel.createAttendance()
            this.mListener.onComplete(attendance)
            this.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val onCancelTapped = Observer<Void> {
        this.dismiss()
    }

    private val onError = Observer<Int> {
        Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
    }

    private val onCoursesListChanged = Observer<List<CourseEntry>> { coursesList ->
        val arrayOfCourses = Array(coursesList.size) {coursesList[it].name}
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, arrayOfCourses
        )
        val validator = ClassValidator(arrayAdapter)
        binding.autoCompleteNameTextInput.setAdapter(arrayAdapter)
        binding.autoCompleteNameTextInput.validator = validator
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAttendanceBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        val modalBottomSheetBehavior = (this.dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.skipCollapsed = true
        modalBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.autoCompleteNameTextInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                (view as AutoCompleteTextView).performValidation()
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = parentFragment as OnCompleteListener
        } catch (error: ClassCastException) {
            throw ClassCastException(parentFragment.toString() + " must implement OnCompleteListener")
        }
    }

    private fun connectViewModelEvents() {
        viewModel.onStart.observe(viewLifecycleOwner, onStartTapped)
        viewModel.onCancel.observe(viewLifecycleOwner, onCancelTapped)
        viewModel.onError.observe(viewLifecycleOwner, onError)
        viewModel.onCoursesWhereTeacher.observe(viewLifecycleOwner, onCoursesListChanged)
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    companion object {
        fun newInstance(bundle: Bundle): DialogAttendanceFragment {
            val fragment = DialogAttendanceFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}