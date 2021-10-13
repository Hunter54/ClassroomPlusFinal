package com.ionutv.classroomplus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentClassesTabBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.Constants.ARG_POSITION
import com.ionutv.classroomplus.adapters.TimeTableRecyclerViewAdapter
import com.ionutv.classroomplus.dialogs.SelectedTimeTableDialogFragment
import com.ionutv.classroomplus.dialogs.TimeTableDialogFragment
import com.ionutv.classroomplus.factories.ClassTabViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.models.TimeTableListObject
import com.ionutv.classroomplus.viewmodels.ClassesTabViewModel

class ClassesTabFragment : BaseFragment(),
    TimeTableRecyclerViewAdapter.IOnTimeTableItemClickListener,
    SelectedTimeTableDialogFragment.OnCompleteListener, TimeTableDialogFragment.OnCompleteListener {

    private lateinit var binding: FragmentClassesTabBinding
    private lateinit var adapter: TimeTableRecyclerViewAdapter
    private lateinit var selectedWeek: String
    private val viewModel: ClassesTabViewModel by viewModels {
        ClassTabViewModelFactory(
            App.appContainer.timeTableRepository,
            selectedWeek
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedPage = arguments?.takeIf { it.containsKey(ARG_POSITION) }?.run {
            getInt(ARG_POSITION)
        }
        selectedWeek = when (selectedPage) {
            1 -> "Odd"
            else -> "Even"
        }
        adapter = TimeTableRecyclerViewAdapter(this)
        viewModel.onClassesList.observe(this, onClassesList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentClassesTabBinding.inflate(inflater)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.rvClassesList.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    private val onClassesList = Observer<List<TimeTableEntry>> { timeTableList ->

        if (timeTableList.isNotEmpty()) {
            binding.tvFragment.visibility = View.GONE
            val recyclerViewTimetableList = generateRecyclerViewList(timeTableList)
            adapter.setItems(recyclerViewTimetableList)
        } else {
            adapter.setItems(mutableListOf())
            binding.tvFragment.visibility = View.VISIBLE
        }
    }

    private fun generateRecyclerViewList(timeTableList: List<TimeTableEntry>): MutableList<TimeTableListObject> {
        val recyclerViewTimetableList = mutableListOf<TimeTableListObject>()
        val map = timeTableList.groupBy { it.day }
        map.keys.forEach { day ->
            recyclerViewTimetableList.add(TimeTableListObject.Day(day))
            map[day]?.forEach {
                recyclerViewTimetableList.add(TimeTableListObject.TimeTable(it))
            }
        }
        return recyclerViewTimetableList
    }

    private val onError = Observer<Int> {
        Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
    }

    override fun onTimeTableItemCLicked(itemId: Int) {
        childFragmentManager.let {
            val bundle = Bundle()
            viewModel.getClass(itemId)
            bundle.putInt(Constants.ARG_SELECTED_ID, itemId)
            SelectedTimeTableDialogFragment.newInstance(bundle).apply {
                show(it, Constants.DIALOG_FRAGMENT_OPTIONS_TAG)
            }
        }
    }

    override fun onEditPress(itemId: Int) {
        childFragmentManager.let {
            val bundle = Bundle()
            bundle.putInt(Constants.ARG_SELECTED_ID, itemId)
            TimeTableDialogFragment.newInstance(bundle).apply {
                show(it, Constants.DIALOG_FRAGMENT_SAVE_TAG)
            }
        }
    }

    override fun onDeletePress() {
        Snackbar.make(binding.root, "Deleted class",7500)
            .setAction(getString(R.string.undo_key)) {
                viewModel.undoDelete()
            }
            .show()
    }

    override fun onSavePress(timeTable: TimeTableEntry) {
        viewModel.editClass(timeTable)
    }

    override fun connectViewModelEvents() {
        viewModel.onError.observe(this, onError)
    }

    override fun disconnectViewModelEvents() {
        viewModel.onError.removeObserver(onError)
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