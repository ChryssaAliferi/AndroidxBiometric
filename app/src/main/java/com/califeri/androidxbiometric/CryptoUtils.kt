package com.califeri.androidxbiometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricPrompt
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val DEFAULT_KEYSTORE = "AndroidKeyStore"
private const val DEFAULT_KEY_NAME = "biometrics_key"

class CryptoUtils {

    fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, DEFAULT_KEYSTORE)
        keyGenerator.init(getKeyGen())
        keyGenerator.generateKey()
    }

    fun getCrypto(): BiometricPrompt.CryptoObject? {
        return try {
            val cipher = getCipher()
            val key = getKey()
            cipher.init(Cipher.ENCRYPT_MODE, key)
            BiometricPrompt.CryptoObject(cipher)
        } catch (e: KeyPermanentlyInvalidatedException) {
            Log.d(TAG, "Key permanently invalidated")
            val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE)
            keyStore.load(null)
            keyStore.deleteEntry(DEFAULT_KEY_NAME)
            null
        } catch (e: NullPointerException) {
            Log.d(TAG, "SecretKey not created")
            val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE)
            keyStore.load(null)
            keyStore.deleteEntry(DEFAULT_KEY_NAME)
            null
        }
    }

    private fun getKeyGen(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            DEFAULT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build()
    }

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(DEFAULT_KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}
