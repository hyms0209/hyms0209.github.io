package com.hae.eforest

import com.fasterxml.jackson.annotation.JsonProperty.Access
import javax.inject.Inject

interface SumSubRepository{
    suspend fun accessToken(param: RequestAccessTokenParam): Resource<AccessTokenResult>
}

class SumSubRepositoryImpl @Inject constructor(
    private val sumSubDataSource: SumSubDataSource
) : SumSubRepository {
    override suspend fun accessToken(param: RequestAccessTokenParam): Resource<AccessTokenResult> {
        return sumSubDataSource.accessToken(param).run {
            matcher {
                AccessTokenResult()
            }
        }
    }
}