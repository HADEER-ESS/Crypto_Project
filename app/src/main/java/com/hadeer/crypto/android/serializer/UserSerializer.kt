package com.hadeer.crypto.android.serializer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.Serializer
import com.hadeer.crypto.android.CryptoManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


@RequiresApi(Build.VERSION_CODES.M)
class UserSerializer (
    private val cryptoGraph : CryptoManager
) : Serializer<UserSetting> {
    override val defaultValue: UserSetting get() = UserSetting()

    override suspend fun readFrom(input: InputStream): UserSetting {
        val decryptedBytes = cryptoGraph.decrypty(input)
        return try {
            Json.decodeFromString(
                deserializer = UserSetting.serializer(),
                string = decryptedBytes.decodeToString()
            )
        }catch(error : SerializationException){
            error.printStackTrace()
            defaultValue
        }
    }


    override suspend fun writeTo(t: UserSetting, output: OutputStream) {
        cryptoGraph.encrypt(
            bytes = Json.encodeToString(
                serializer = UserSetting.serializer(),
                value = t
            ).encodeToByteArray()
            ,
            outStream = output
        )
    }
}