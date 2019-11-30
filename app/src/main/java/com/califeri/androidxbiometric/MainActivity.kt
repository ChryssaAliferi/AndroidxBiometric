package com.califeri.androidxbiometric

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var button: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
    }

    private fun initializeViews() {
        button = findViewById(R.id.mb_use_biometric)
        button.setOnClickListener {
            showBiometricPrompt()
        }
    }

    private fun showBiometricPrompt() {
        val biometricPromptUtils = BiometricPromptUtils(this, object : BiometricPromptUtils.BiometricListener {
            override fun onAuthenticationLockoutError() {
            }

            override fun onAuthenticationPermanentLockoutError() {
            }

            override fun onAuthenticationSuccess() {
            }

            override fun onAuthenticationFailed() {
            }

            override fun onAuthenticationError() {
            }
        })
        biometricPromptUtils.showBiometricPrompt(
            resources.getString(R.string.confirmYourBiometricsKey),
            resources.getString(R.string.cancelKey)
        )
    }
}
