package com.hae.eforest
import com.google.gson.annotations.SerializedName

data class FCMResult(
    @SerializedName("OS")
    var OS: String = "",
    @SerializedName("PACKAGE")
    var PACKAGE: String = "",
    @SerializedName("REQUEST_VERSION")
    var REQUEST_VERSION: String = "",
    @SerializedName("NEW_VERSION")
    var NEW_VERSION: String = "",
    @SerializedName("UPDATE_YN")
    var UPDATE_YN: String = "",
    @SerializedName("FORCE_YN")
    var FORCE_YN: String = ""
)
