package com.hae.eforest

object RemoteError {
    // HTTP STATUS
    const val HTTP_STATUS_OK = 200

    const val ERR_CODE_OK = 0

    const val ERR_CODE_FAIL_TOKEN = 401
    const val ERR_CODE_FAIL_TOKEN_ = 410

    const val ERR_CODE_DATA_NULL = 999

    // ERROR INTERNAL : 네트워크 요청중 모바일단 내부 오류
    const val ERR_CODE_INTERNAL = 10401
}