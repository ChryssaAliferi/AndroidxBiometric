# AndroidXBiometric

AndroidXBiometric is an example of the usage of androidx Biometric API, using a `BiometricPromptUtils.kt` class that wraps up the main methods for building a biometric prompt.

With the launch of Android 10 (API level 29), developers can now use the Biometric API, part of the AndroidX Biometric Library, for all their on-device user authentication needs.
Biometric API provides a standardised dialog that works out of the box across multiple Android versions and persists across orientation changes. Also, it keeps things simple by only requiring you to provide the text that will be displayed to the user and a few callbacks for authentication success or failure.

## Usage

```kotlin
private fun showBiometricPrompt() {
        val biometricPromptUtils = BiometricPromptUtils(this, object : BiometricPromptUtils.BiometricListener {
            override fun onAuthenticationLockoutError() {
                // implement your lockout error UI prompt
            }

            override fun onAuthenticationPermanentLockoutError() {
                // implement your permanent lockout error UI prompt
            }

            override fun onAuthenticationSuccess() {
                // implement your authentication success UI prompt
            }

            override fun onAuthenticationFailed() {
                // implement your authentication failed UI prompt
            }

            override fun onAuthenticationError() {
                // implement your authentication error UI prompt
            }
        })
        biometricPromptUtils.showBiometricPrompt(
            resources.getString(R.string.confirmYourBiometricsKey),
            resources.getString(R.string.cancelKey),
            confirmationRequired = false
        )
    }
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
