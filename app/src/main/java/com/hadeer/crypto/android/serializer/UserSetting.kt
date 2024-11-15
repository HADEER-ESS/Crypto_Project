package com.hadeer.crypto.android.serializer

import kotlinx.serialization.Serializable

@Serializable
data class UserSetting(
    val userName : String? = "",
    val password : String? = ""
)
