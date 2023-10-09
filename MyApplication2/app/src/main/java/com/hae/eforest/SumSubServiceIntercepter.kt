package com.hae.eforest.data.service.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.hae.eforest.RemoteError
import com.hae.eforest.java.AppTokenJavaExample
import com.hae.eforest.java.model.HttpMethod
import okhttp3.*
import okhttp3.MediaType.Companion.get
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.apache.commons.codec.binary.Hex
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Access Token 관리 및 갱신 프로세스
 * HTTP Status 처리
 */
class SumSubServiceIntercepter: Interceptor {
    private val TAG = this::class.java.simpleName

    private val NO_ACCESSTOKEN = "na" // 토큰 없음
    private val EMPTY_JSON = "{}"
    private val FIELD_ERR_CODE = "errCode"
    private val FIELD_DATA = "data"
    private val FIELD_ACCESSTOKEN = "accessToken"
    private val FIELD_REFRESHTOKEN = "refreshToken"

    private val SUMSUB_SECRET_KEY = "dh1OpJubkrClT0j8jK3BywnijsakBATI"

    private val SUMSUB_APP_TOKEN = "prd:MciACqgeccqGsGyfkFjh3w6X.LxzhZA9HbeLtq1NsXO5Ie8htOD6VQiIe"

    /***
     * 요청에 대한 인터셉터 처리 ( 토큰 갱신 및 요청전 처리를 이곳에서 처리)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        var buffer = Buffer()
        originalRequest.body?.writeTo(buffer)

        var urlPath = originalRequest.url.encodedPath
        var isFirst = true
        var fullPath = originalRequest.url.toString().replace("https://api.sumsub.com", "")

        Log.d(TAG, "originalRequest : ${originalRequest.body}")

        val sig = createSignature(
            getTimeStamp(),
            originalRequest.method,
            fullPath,
            requestBodyToBytes(RequestBody.create(null, ByteArray(0)))
        )

        var newRequest = originalRequest
            .newBuilder()
            .addHeader("X-App-Token", SUMSUB_APP_TOKEN)
            .addHeader("X-App-Access-Sig", sig ?: "")
            .addHeader("X-App-Access-Ts", getTimeStamp().toString())
            .addHeader("Content-Type","application/json")
            .build()

        try {
            //  동기로 요청 처리
            val response: Response = chain.proceed(newRequest)
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

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun createSignature(ts: Long, httpMethod: String, path: String, body: ByteArray?): String {
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        hmacSha256.init(SecretKeySpec(SUMSUB_SECRET_KEY.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))

        val encryptStr = "$ts${httpMethod}$path"
        hmacSha256.update(encryptStr.toByteArray(StandardCharsets.UTF_8))

        return if (body == null) {
            Hex.encodeHexString(hmacSha256.doFinal())
        } else {
            hmacSha256.update(body)
            Hex.encodeHexString(hmacSha256.doFinal())
        }
    }

    @Throws(IOException::class)
    fun requestBodyToBytes(requestBody: RequestBody): ByteArray? {
        val buffer = Buffer()
        requestBody.writeTo(buffer)

        return buffer.readByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeStamp(): Long = Instant.now().epochSecond

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