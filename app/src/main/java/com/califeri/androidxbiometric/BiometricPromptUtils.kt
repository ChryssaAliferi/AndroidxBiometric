package com.califeri.androidxbiometric

import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

class BiometricPromptUtils(
    private val fragmentActivity: FragmentActivity,
    private val biometricListener: BiometricListener? = null
) {
    fun showBiometricPrompt(title: String, negativeText: String, confirmationRequired: Boolean) {
        if (canAuthenticate()) {
            createBiometricPrompt(fragmentActivity).authenticate(
                createBiometricPromptInfo(title, negativeText, confirmationRequired)
            )
        }
    }

    private fun createBiometricPromptInfo(
        title: String,
        negativeText: String,
        confirmationRequired: Boolean
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setNegativeButtonText(negativeText)
            .setConfirmationRequired(confirmationRequired)
            .build()
    }

    private fun createBiometricPrompt(fragmentActivity: FragmentActivity): BiometricPrompt {

        val executor = Executors.newSingleThreadExecutor()
        return BiometricPrompt(fragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                resolveAuthenticationError(errorCode)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                biometricListener?.onAuthenticationSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                biometricListener?.onAuthenticationFailed()
            }

            private fun resolveAuthenticationError(errorCode: Int) {
                when (errorCode) {
                    BiometricConstants.ERROR_LOCKOUT -> biometricListener?.onAuthenticationLockoutError()
                    BiometricConstants.ERROR_LOCKOUT_PERMANENT -> biometricListener?.onAuthenticationPermanentLockoutError()
                    else -> biometricListener?.onAuthenticationError()
                }
            }
        })
    }

    // You can use the same method in order to know beforehand if your device has biometric hardware
    // or if the user has biometric data enrolled
    fun canAuthenticate(): Boolean {
        when (BiometricManager.from(fragmentActivity).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> return true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> return false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> return false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return false
        }
        return false
    }

    interface BiometricListener {
        fun onAuthenticationSuccess()
        fun onAuthenticationFailed()
        fun onAuthenticationError()
        fun onAuthenticationLockoutError()
        fun onAuthenticationPermanentLockoutError()
    }
}
