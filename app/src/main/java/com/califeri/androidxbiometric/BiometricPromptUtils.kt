package com.califeri.androidxbiometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT_PERMANENT
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

class BiometricPromptUtils(
    private val fragmentActivity: FragmentActivity,
    private val biometricListener: BiometricListener? = null,
    private val cryptoUtils: CryptoUtils
) {

    fun generateCryptoKey() = cryptoUtils.generateKey()

    fun showBiometricPrompt(title: String, negativeText: String, confirmationRequired: Boolean) {
        if (isDeviceSupportingBiometrics()) {
            cryptoUtils.getCrypto(object : CryptoUtils.SecretKeyCreationListener {
                override fun onCryptoObjectCreated(cryptoObject: BiometricPrompt.CryptoObject) {
                    createBiometricPrompt(fragmentActivity)
                        .authenticate(createBiometricPromptInfo(title, negativeText, confirmationRequired), cryptoObject)
                }

                override fun onKeyStorePermanentlyInvalidated() {
                    biometricListener?.onNewBiometricEnrollment()
                }

                override fun onSecretedKeyNotCreated() {
                    biometricListener?.onFirstBiometricAuthentication()
                }
            })
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
                    ERROR_LOCKOUT -> biometricListener?.onAuthenticationLockoutError()
                    ERROR_LOCKOUT_PERMANENT -> biometricListener?.onAuthenticationPermanentLockoutError()
                    else -> biometricListener?.onAuthenticationError()
                }
            }
        })
    }

    private fun isDeviceSupportingBiometrics(): Boolean {
        return when (BiometricManager.from(fragmentActivity)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    interface BiometricListener {

        fun onAuthenticationSuccess()
        fun onAuthenticationFailed()
        fun onAuthenticationError()
        fun onAuthenticationLockoutError()
        fun onAuthenticationPermanentLockoutError()
        fun onNewBiometricEnrollment()
        fun onFirstBiometricAuthentication()
    }
}
