package com.hae.eforest

object ResError {
    const val ERR_UNKNOWN = -1

    const val ERR_FAIL_TOKEN = 401
    const val ERR_FAIL_TOKEN2 = 410

    // ERROR INTERNAL : 네트워크 요청중 모바일단 내부 오류
    const val ERR_INTERNAL = 10401

    // todo ex) http 503
    const val ERR_SERVICE_UNAVAILABLE = 20503
}