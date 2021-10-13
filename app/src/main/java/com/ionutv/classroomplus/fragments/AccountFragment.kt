package com.ionutv.classroomplus.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.FragmentAccountBinding
import com.ionutv.classroomplus.activities.MainActivity
import com.ionutv.classroomplus.factories.AccountViewModelFactory
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.viewmodels.AccountViewModel
import kotlin.system.exitProcess


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AccountViewModel by viewModels {
        AccountViewModelFactory(
            App.appContainer.classRoomRepository,
            App.appContainer.timeTableRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.btnLogOut.setOnClickListener { viewModel.signOut() }
        setHasOptionsMenu(true)
        return binding.root
    }

    private val authStateListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                viewModel.completeLogOut()
            }
        }

    private val onSignOut = Observer<Void>{
        val context = requireActivity().applicationContext
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val logOutIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(logOutIntent)
        exitProcess(0)
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

    override fun onResume() {
        super.onResume()
        viewModel.onLogOut.observe(viewLifecycleOwner,onSignOut)
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_menu, menu)
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}