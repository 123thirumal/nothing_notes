package com.example.mynotes.utils

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


//for auth

fun authenticateUser(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (Int, CharSequence) -> Unit,
    onFailed: () -> Unit
) {

    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        })

    val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Private Files")
        .setSubtitle("Use your fingerprint or phone password to unlock")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        promptInfoBuilder.setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    } else {
        @Suppress("DEPRECATION")
        promptInfoBuilder.setDeviceCredentialAllowed(true)
    }

    biometricPrompt.authenticate(promptInfoBuilder.build())
}