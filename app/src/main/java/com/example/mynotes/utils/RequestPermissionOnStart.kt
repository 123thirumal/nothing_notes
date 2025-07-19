package com.example.mynotes.utils

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissionsOnStart() {
    val context = LocalContext.current

    val microphonePermission = android.Manifest.permission.RECORD_AUDIO

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.all { it.value }
        Toast.makeText(context, if (allGranted) "Permissions granted" else "Some permissions denied", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            microphonePermission
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            permissionLauncher.launch(arrayOf(microphonePermission))
        }
    }
}