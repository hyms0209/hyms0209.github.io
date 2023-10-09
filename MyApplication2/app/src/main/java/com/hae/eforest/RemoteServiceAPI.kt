package com.hae.eforest

import com.hae.eforest.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RemoteServiceAPI {
    /**
     * FCM 전송
     */
    @POST("/fcm/send")
    suspend fun fcmSend(
        @Body requestFCMParam: RequestFCMParam
    ) : Response<FCMResult>
}