package ru.mironov.currencyconverter.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.inject.Singleton
import androidx.annotation.NonNull
import javax.crypto.spec.GCMParameterSpec


@RequiresApi(Build.VERSION_CODES.M)
@Singleton
class Cryptography constructor(keyName: String?) {

    companion object {
        private const val TRANSFORMATION =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val SEPARATOR = ","
    }

    private var keyName: String? = null
    private var keyStore: KeyStore? = null
    private var secretKey: SecretKey? = null

    init {
        this.keyName = keyName
        initKeystore()
        loadOrGenerateKey()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun loadOrGenerateKey() {
        getKey()
        if (secretKey == null) generateKey()
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )
    private fun initKeystore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }
    }

    private fun getKey() {
        try {
            val secretKeyEntry = keyStore?.getEntry(keyName, null)
            if (secretKeyEntry is KeyStore.SecretKeyEntry) {
                secretKey = secretKeyEntry.secretKey
            }
        } catch (e: KeyStoreException) {
            // failed to retrieve -> will generate new
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnrecoverableEntryException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun generateKey() {
        val keyGenerator: KeyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyName!!,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        secretKey = keyGenerator.generateKey()
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encrypt(toEncrypt: String): String {
        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv: String = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        val encrypted: String = Base64.encodeToString(
            cipher.doFinal(toEncrypt.toByteArray(StandardCharsets.UTF_8)),
            Base64.DEFAULT
        )
        return encrypted + SEPARATOR + iv
    }


    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(toDecrypt: String): String {
        val parts = toDecrypt.split(SEPARATOR).toTypedArray()
        if (parts.size != 2) throw AssertionError("String to decrypt must be of the form: 'BASE64_DATA" + SEPARATOR + "BASE64_IV'")
        val encrypted: ByteArray = Base64.decode(parts[0], Base64.DEFAULT)
        val iv: ByteArray = Base64.decode(parts[1], Base64.DEFAULT)
        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return String(cipher.doFinal(encrypted), StandardCharsets.UTF_8)
    }


}