package com.hae.eforest

import com.google.gson.annotations.SerializedName

data class AccessTokenResult(
@SerializedName("token")
var token: String = "",
@SerializedName("userId")
var userId: String = "",
)
