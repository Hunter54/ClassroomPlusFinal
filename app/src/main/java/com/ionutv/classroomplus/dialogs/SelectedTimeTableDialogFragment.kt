package com.ionutv.classroomplus.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ionutv.classroomplus.databinding.DialogSelectedTimetableBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.viewmodels.SelectedTimeTableDialogViewModel

class SelectedTimeTableDialogFragment : BottomSheetDialogFragment() {

    interface OnCompleteListener {
        fun onEditPress(itemId : Int)
        fun onDeletePress()
    }

    private lateinit var binding: DialogSelectedTimetableBinding
    private var mListener: OnCompleteListener? = null
    private val viewModel: SelectedTimeTableDialogViewModel by viewModels()
    private var selectedId: Int = 0

    private val onEdit = Observer<Void> {
        this.mListener?.onEditPress(selectedId)
        this.dismiss()
    }

    private val onDelete = Observer<Void> {
        viewModel.deleteClass(selectedId)
        mListener?.onDeletePress()
        this.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedId = arguments?.takeIf { it.containsKey(Constants.ARG_SELECTED_ID) }?.run {
            getInt(Constants.ARG_SELECTED_ID)
        } ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogSelectedTimetableBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return binding.root
    }

    private fun connectViewModelEvents() {
        viewModel.onDeleteTapped.observe(this, onDelete)
        viewModel.onEditTapped.observe(this, onEdit)
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            mListener = parentFragment as OnCompleteListener
        } catch ( error : ClassCastException){
            throw ClassCastException(parentFragment.toString() + " must implement OnCompleteListener")
        }
    }

    companion object {
        fun newInstance(bundle: Bundle): SelectedTimeTableDialogFragment {
            val fragment = SelectedTimeTableDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}