package com.hae.eforest.data.service.network

import android.util.Log
import com.hae.eforest.AppApplication
import com.hae.eforest.RemoteError
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

/**
 * Access Token 관리 및 갱신 프로세스
 * HTTP Status 처리
 */
class RemoteServiceIntercepter: Interceptor {
    private val TAG = this::class.java.simpleName

    private val NO_ACCESSTOKEN = "na" // 토큰 없음
    private val EMPTY_JSON = "{}"
    private val FIELD_ERR_CODE = "errCode"
    private val FIELD_DATA = "data"
    private val FIELD_ACCESSTOKEN = "accessToken"
    private val FIELD_REFRESHTOKEN = "refreshToken"

    /***
     * 요청에 대한 인터셉터 처리 ( 토큰 갱신 및 요청전 처리를 이곳에서 처리)
     */
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
            .newBuilder()
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization", "key=AAAAsYcU0_0:APA91bFEntTqdb5pbhcvBQX1vK9c-_XDmZ8ga1xfN5geMw1Rk0-dLavcfEuGHjeaP_om9qoe99uvp0-duiWDVz4md0xkKBkmNz_7vUuu6BYSsJ0WhHqx0vp7j7O0WjK1seJ8a0wD3PVf")
            .build()

        try {
            //  동기로 요청 처리
            val response: Response = chain.proceed(originalRequest)
            val jsonBody = response.extractResponseJson()
            //API 서버 규격상 에러코드 없음
            //val errCode = jsonBody.findErrCode()
            val errCode = RemoteError.ERR_CODE_OK

            if (response.code == RemoteError.HTTP_STATUS_OK
                && errCode == RemoteError.ERR_CODE_OK
            ) {
                try {
                    return safeProceed(
                        response,
                        jsonBody
                    )

                } catch (e: Exception) {
                    return safeProceed(
                        response,
                        null
                    )
                }
            } else {
                return safeProceed(
                    response,
                    null
                )
            }

        } catch (e: Exception) {
            // Interceptor단 에러
            val errJson = JSONObject().apply {
                put("status", RemoteError.HTTP_STATUS_OK)
                put("errCode", RemoteError.ERR_CODE_INTERNAL)
                put("errMsg", "${e::class.simpleName}: ${e.message}")
            }
            return Response.Builder()
                .code(RemoteError.HTTP_STATUS_OK)
                .request(originalRequest)
                .message(e.message ?: "")
                .body(errJson.toString().toResponseBody("application/json".toMediaType()))
                .protocol(Protocol.HTTP_1_1)
                .build()

        }
    }


    /**
     * http status code가 200이 아닌경우
     * http status의 처리 프로세스가 별도로 없으므로
     * 일단 status를 Response 객체에 저장하고
     * Repository 혹은 Usecase 등에서 처리
     */
    private fun safeProceed(response: Response, jsonBody: JSONObject?): Response {
        if (jsonBody != null) {
            jsonBody
                .put("status", response.code)
                .toString()
        } else {
            response
                .extractResponseJson()
                .put("status", response.code)
                .toString()
        }.let { jsonString ->
            return response.newBuilder()
                .code(RemoteError.HTTP_STATUS_OK)
                .body(jsonString.toResponseBody("application/json".toMediaType()))
                .build()
        }
    }

    /*
        private fun safeProceed(response: Response, body: ResponseBody?): Response {
            if (response.code == Error.HTTP_STATUS_OK) {

                if (body != null) {
                    return response.newBuilder()
                        .body(body)
                        .build()
                } else {
                    return response
                }

            } else {
                return response
                    .extractResponseJson()
                    .put("status", response.code)
                    .toString().let { json ->
                        response.newBuilder()
                            .code(Error.HTTP_STATUS_OK)
                            .body(body ?: json.toResponseBody("application/json".toMediaType()))
                            .build()
                }
            }
        }
    */
    @Throws(Exception::class)
    private fun Response.extractResponseJson(): JSONObject {
        val jsonBody = this.body?.string()
        Log.d(TAG, "response body : $jsonBody")
        return JSONObject(jsonBody ?: EMPTY_JSON)
    }

    @Throws(Exception::class)
    private fun JSONObject.findErrCode(): Int? {
        return getInt(FIELD_ERR_CODE)
    }

    @Throws(Exception::class)
    private fun JSONObject.findAccessToken(): String {
        return getJSONObject(FIELD_DATA).getString(FIELD_ACCESSTOKEN) ?: ""
    }

    @Throws(Exception::class)
    private fun JSONObject.findRefreshToken(): String {
        return getJSONObject(FIELD_DATA).getString(FIELD_REFRESHTOKEN) ?: ""
    }

}