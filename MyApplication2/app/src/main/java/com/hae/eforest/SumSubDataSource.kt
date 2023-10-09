package com.hae.eforest

import com.hae.eforest.FCMResult
import com.hae.eforest.RemoteServiceAPI
import com.hae.eforest.RequestFCMParam

import javax.inject.Inject

interface SumSubDataSource {
    /// 버젼정보 취득
    suspend fun accessToken(param: RequestAccessTokenParam): Response<AccessTokenResult>
}

class SumSubDataSourceImpl @Inject constructor(
    private val sumSubServiceAPI: SumSubServiceAPI
): SumSubDataSource {

    override suspend fun accessToken(param: RequestAccessTokenParam): Response<AccessTokenResult> {
        return sumSubServiceAPI.accessToken(
            param.userId,
            param.levelName
        )
    }
}