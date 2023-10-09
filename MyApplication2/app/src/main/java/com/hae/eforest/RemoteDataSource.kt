package com.hae.eforest

import com.hae.eforest.FCMResult
import com.hae.eforest.RemoteServiceAPI
import com.hae.eforest.RequestFCMParam

import javax.inject.Inject

interface RemoteDataSource {
    /// 버젼정보 취득
    suspend fun fcmSend(param: RequestFCMParam): Response<FCMResult>
}

class RemoteDataSourceImpl @Inject constructor(
    private val remoteServiceAPI: RemoteServiceAPI
): RemoteDataSource {

    override suspend fun fcmSend(param: RequestFCMParam): Response<FCMResult> {
        return remoteServiceAPI.fcmSend(
            param
        )
    }
}