package com.ionutv.classroomplus.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.ActivityMainBinding
import com.ionutv.classroomplus.factories.MainActivityViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.viewmodels.MainActivityViewModel
import com.vladan.networkchecker.NetworkLiveData
import com.vladan.networkchecker.NetworkState


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            App.appContainer.classRoomRepository,
            App.appContainer.sheetsRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).initAppContainer()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.initializeCourses()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navigationController = navHostFragment.navController

        binding.userMainScreenNavigationView.setupWithNavController(navigationController)
        binding.viewModel = viewModel

        val topLevelDestination = setOf(
            R.id.classesFragment,
            R.id.attendanceFragment,
            R.id.manageAttendancesFragment,
            R.id.accountFragment
        )

        appBarConfiguration =
            AppBarConfiguration(topLevelDestination, binding.userMainScreenDrawerLayout)
        setupActionBarWithNavController(navigationController, appBarConfiguration)
    }

    override fun connectViewModelEvents() {
        viewModel.allCourses.observe(this, {
            val item = it
        })
        NetworkLiveData.get().observe(this, onNetworkChange)
    }

    private val onNetworkChange = Observer<NetworkState> {
        Toast.makeText(this, "Connection is " + it.isConnected, Toast.LENGTH_SHORT).show()
        if (it.isConnected) {
            binding.tvNoInternet.visibility = View.GONE
        } else {
            binding.tvNoInternet.visibility = View.VISIBLE
        }
    }

    fun refreshClassroom() {
        viewModel.initializeCourses()
    }

    override fun disconnectViewModelEvents() {
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }
}