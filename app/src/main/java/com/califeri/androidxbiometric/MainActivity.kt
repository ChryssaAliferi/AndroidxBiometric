package com.califeri.androidxbiometric

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

const val TAG = "AndroidxBiometric"

class MainActivity : AppCompatActivity() {

    private lateinit var button: MaterialButton
    private lateinit var createCryptoButton: MaterialButton
    private val biometricPromptUtils = BiometricPromptUtils(this, object : BiometricPromptUtils.BiometricListener {
        override fun onAuthenticationLockoutError() {
            Log.d(TAG, "onAuthenticationLockoutError")
        }

        override fun onAuthenticationPermanentLockoutError() {
            Log.d(TAG, "onAuthenticationPermanentLockoutError")
        }

        override fun onNewBiometricEnrollment() {
            Log.d(TAG, "onNewBiometricEnrollment")
        }

        override fun onAuthenticationSuccess() {
            Log.d(TAG, "onAuthenticationSuccess")
        }

        override fun onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed")
        }

        override fun onAuthenticationError() {
            Log.d(TAG, "onAuthenticationError")
        }
    }, CryptoUtils())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
    }

    private fun initializeViews() {
        button = findViewById(R.id.mb_use_biometric)
        button.setOnClickListener {
            biometricPromptUtils.showBiometricPrompt(
                resources.getString(R.string.confirmYourBiometricsKey),
                resources.getString(R.string.cancelKey),
                confirmationRequired = false
            )
        }

        createCryptoButton = findViewById(R.id.mb_create_crypto_object)
        createCryptoButton.setOnClickListener {
            biometricPromptUtils.generateCryptoKey()
        }
    }
}
