package com.hae.eforest

import com.hae.eforest.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SumSubServiceAPI {
    /**
     * AccessToken 취득
     */
    @POST("/resources/accessTokens")
    suspend fun accessToken(
        @Query("userId") userId: String,
        @Query("levelName") levelName: String,
    ) : Response<AccessTokenResult>
}