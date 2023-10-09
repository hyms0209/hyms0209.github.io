package com.hae.eforest

data class RequestFCMParam(
    val to: String,
    val data: RequestFCMParam.FCMData
) {
    data class FCMData(
        val title: String,
        val message: String
    )
}