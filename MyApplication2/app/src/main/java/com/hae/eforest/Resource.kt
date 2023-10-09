package com.hae.eforest

sealed class Resource<T>(
    val data: T? = null,
    val errCode: Int? = null,
    val errMsg: String? = null,
) {
    class Success<T>(data: T, errCode: Int = 0) : Resource<T>(data, 0)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errCode: Int, errMsg: String? = null) : Resource<T>(null, errCode, errMsg)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is DataError -> "Error[error=$errCode|$errMsg]"
            is Loading<T> -> "Loading.."
        }
    }

    fun isLoaded(): Boolean {
        return this is Success && data != null
    }
}