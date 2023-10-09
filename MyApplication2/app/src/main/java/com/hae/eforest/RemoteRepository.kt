package com.hae.eforest

import javax.inject.Inject

interface RemoteRepository{
    suspend fun sendFCM(param: RequestFCMParam): Resource<FCMResult>
}

class RemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : RemoteRepository {
    override suspend fun sendFCM(param: RequestFCMParam): Resource<FCMResult> {
        return remoteDataSource.fcmSend(param).run {
            matcher {
                FCMResult()
            }
        }
    }
}