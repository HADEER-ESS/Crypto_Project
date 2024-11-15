package com.hadeer.crypto.android

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {
    //1
    private val keystor = KeyStore.getInstance("AndroidKeyStore")
        .apply {
//            initial value
            load(null)
        }

    //3
    //Create a Cipher for Encryption and Decryption
    private val encryptCipher = Cipher.getInstance(TRANSFORMATION)
        .apply {
//            init take (Cipher mode [encryption || decryption], key => Secret key)
            init(
                Cipher.ENCRYPT_MODE,
                getKey()
            )
        }

//    3.a Function to check if the key is already exist
//    Yes => return it
//    No => generate one
    private fun getKey():SecretKey{
        val existingKey = keystor.getEntry("secret", null) as? KeyStore.SecretKeyEntry
    return existingKey?.secretKey ?: generateKey()
    }
    private fun generateKey():SecretKey{
        val secretKey = KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder("secret", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }
        return secretKey.generateKey()
    }

    //4
//    Create Cipher for Decription and it require a function
    private fun getDecryptCipherForIv(iv : ByteArray) : Cipher{
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(
                Cipher.DECRYPT_MODE,
                getKey(),
                IvParameterSpec(iv)
            )
        }
    }

    //2
    companion object{
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC

        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

        /*{
        Block Mode:
        describes how the different blocks of a multi-block plaintext
        should be encrypted and decrypted
        Padding:
        some modes (such as ECB and CBC) require that the final block to be padded
        before encryption
        }*/

    }

    // 5
//    Generate functions that will encrypt and decrypt my code (public class methods
    fun encrypt(bytes : ByteArray, outStream : OutputStream) : ByteArray{//understandable text => un-understandable text

        val encryptBytes = encryptCipher.doFinal(bytes)
        outStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptBytes.size)
            it.write(encryptBytes)
        }
        return encryptBytes
    }

    fun decrypty(inStream : InputStream): ByteArray{ //un-understandable text => understandable text

        return inStream.use {
            val size = it.read()
            val iv = ByteArray(size)
            it.read(iv)

            val encryptedByteSize = it.read()
            val encryptBytes = ByteArray(encryptedByteSize)
            it.read(encryptBytes)

            getDecryptCipherForIv(iv).doFinal(encryptBytes)
        }

    }
}