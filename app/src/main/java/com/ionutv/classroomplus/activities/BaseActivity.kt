package com.ionutv.classroomplus.activities

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    abstract fun connectViewModelEvents()

    abstract fun disconnectViewModelEvents()

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun hasPermission(permissionType: String): Boolean {
        return when {
            ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(permissionType) -> {
                requestLocationPermission(permissionType)
                false
            }
            else ->{
                requestLocationPermission(permissionType)
                false
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Location permission denied")
                    .setMessage("This feature will be unavailable until you grant location permission from the app settings")
                    .setCancelable( false)
                    .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    }
                    .show()
            }
        }

    private fun requestLocationPermission(permissionType: String) {
        AlertDialog.Builder(this)
            .setTitle("Location permission required")
            .setMessage("Starting from Android M (6.0), the system requires apps to be granted location access in order to scan for BLE devices.")
            .setCancelable( true)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                requestPermissionLauncher.launch(
                   permissionType)
            }
            .setNegativeButton(android.R.string.cancel){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .show()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}