package com.ionutv.classroomplus.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentClassesBinding
import com.ionutv.classroomplus.ui.ClassroomPlusSharedPreferences
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.activities.MainActivity
import com.ionutv.classroomplus.adapters.ClassesViewPagerAdapter
import com.ionutv.classroomplus.dialogs.TimeTableDialogFragment
import com.ionutv.classroomplus.factories.ClassesViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.viewmodels.ClassesViewModel

class ClassesFragment : BaseFragment(), TimeTableDialogFragment.OnCompleteListener {

    private lateinit var binding: FragmentClassesBinding
    private lateinit var classesAdapter: ClassesViewPagerAdapter
    private val viewModel: ClassesViewModel by viewModels { ClassesViewModelFactory(App.appContainer.timeTableRepository) }
    private var tabViewed =
        ClassroomPlusSharedPreferences.getString(Constants.TAB_VIEWED_PREFERENCES_KEY, "ODD")
    private val loadingDialog by lazy {
        SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE).also {
            it.titleText = "Loading"
            it.setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classesAdapter = ClassesViewPagerAdapter(this)

        binding.pagerClasses.adapter = classesAdapter
        TabLayoutMediator(binding.tabLayoutClasses, binding.pagerClasses) { tab, position ->
            tab.text = when (position) {
                0 -> "Odd"
                1 -> "Even"
                else -> "Odd"
            }
            val tabPosition = when (tabViewed) {
                "ODD" -> 0
                "EVEN" -> 1
                else -> 0
            }
            binding.pagerClasses.setCurrentItem(tabPosition, false)
        }.attach()

        binding.pagerClasses.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("Page Change Listener", "Changed tab page")
                tabViewed = when (position) {
                    0 -> "ODD"
                    1 -> "EVEN"
                    else -> "ODD"
                }
            }
        })

        binding.fabClasses.setOnClickListener {
            childFragmentManager.let {
                TimeTableDialogFragment.newInstance(Bundle()).apply {
                    show(it, Constants.DIALOG_FRAGMENT_SAVE_TAG)
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.timetable_menu, menu)
    }

    private val onIsBusy = Observer<Boolean>{
        if(it){
            loadingDialog.show()
        }else{
            loadingDialog.dismiss()
        }
    }

    private fun showBackUpAlertDialog(){
        SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure you want to back up?")
            .setContentText("This will delete previous back up")
            .setConfirmButton("Yes,back up!") {
                it.dismiss()
                viewModel.saveToFirebase()
            }
            .setCancelButton("Cancel"){
                it.dismiss()
            }
            .show()
    }

    private fun showRestoreAlertDialog(){
        SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure you want to restore?")
            .setContentText("This will overwrite all your current entries")
            .setConfirmText("Yes,restore!")
            .setConfirmClickListener {
                it.dismiss()
                viewModel.restoreFromFirebase()
            }
            .setCancelButton("Cancel"){
                it.dismiss()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_classroom -> {
                (requireActivity() as MainActivity).refreshClassroom()
                true
            }
            R.id.back_up_timetable -> {
                showBackUpAlertDialog()
                true
            }
            R.id.restore_timetable ->{
                showRestoreAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSavePress(timeTable: TimeTableEntry) {
        viewModel.addClass(timeTable)
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    override fun onPause() {
        super.onPause()
        ClassroomPlusSharedPreferences.putString(Constants.TAB_VIEWED_PREFERENCES_KEY, tabViewed)
    }

    override fun connectViewModelEvents() {
        viewModel.onIsBusy.observe(viewLifecycleOwner, onIsBusy)
    }

    override fun disconnectViewModelEvents() {
        TODO("Not yet implemented")
    }
}