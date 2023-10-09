package com.hae.eforest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Response<T> (
    @Expose
    @SerializedName("status")
    val status: Int = 200, // http status todo 검토
    @Expose
    @SerializedName("errCode")
    val errCode: Int,
    @Expose
    @SerializedName("errMsg")
    val errMsg: String,
    @Expose
    @SerializedName("ES_RETURN")
    val ES_RETURN: T? = null
) {
    fun isHttpOk(): Boolean = status == RemoteError.HTTP_STATUS_OK

    fun isRespOk(): Boolean = errCode == RemoteError.ERR_CODE_OK

    fun isDataOk(): Boolean = ES_RETURN != null

    fun isOk(): Boolean
            = isHttpOk() && isRespOk() && isDataOk()

    /**
     * 일반적인 케이스
     */
    fun <S> matcher(
        matcher: ((T) -> S)
    ): Resource<S> {

        // HTTP 상태 오류
        return if (status != RemoteError.HTTP_STATUS_OK) {
            Resource.DataError(ResError.ERR_SERVICE_UNAVAILABLE, "[$status] HTTP status error")
        }
        // 앱 Internal 오류 (ex. interceptor단 처리중 Exception 발생)
        else if (errCode == RemoteError.ERR_CODE_INTERNAL) {
            Resource.DataError(ResError.ERR_INTERNAL, errMsg)
        }
        // 서버 오류
        else if (errCode != RemoteError.ERR_CODE_OK) {
            Resource.DataError(errCode, errMsg)
        }
        // 성공 케이스
        else {
            ES_RETURN?.let {
                Resource.Success(matcher(it))
            } ?: Resource.DataError(errCode, errMsg)
        }
    }

    /**
     * 일반적인 케이스
     * - data null 허용
     * ex) errCode로 업무 성공 여부만 판단하는 케이스 일때 data == null
     * -> matcher에서 notnull 반환 시: Resource.DataError(errCode, errMsg)
     * -> matcher에서 null 반환 시: Resource.DataError(errCode, errMsg)
     */
    fun <S> matcherNullable(
        matcher: ((T?) -> S?)
    ): Resource<S> {
        // HTTP 상태 오류
        return if (status != RemoteError.HTTP_STATUS_OK) {
            Resource.DataError(ResError.ERR_SERVICE_UNAVAILABLE, "[$status] HTTP status error")
        }
        // 앱 Internal 오류 (ex. interceptor단 처리중 Exception 발생)
        else if (errCode == RemoteError.ERR_CODE_INTERNAL) {
            Resource.DataError(ResError.ERR_INTERNAL, errMsg)
        }
        // 서버 오류
        else if (errCode != RemoteError.ERR_CODE_OK) {
            Resource.DataError(errCode, errMsg)
        }
        // 성공 케이스
        else {
            ES_RETURN.let {
                matcher(it).let {
                    if (it != null) {
                        Resource.Success(it)
                    } else {
                        Resource.DataError(errCode, errMsg)
                    }
                }
            }
        }
    }
}