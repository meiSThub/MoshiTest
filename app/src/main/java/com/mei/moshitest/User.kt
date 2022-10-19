package com.mei.moshitest

/**
 * Created by mei on 2022/10/19.
 * Description:
 */
data class User(
    val name: String = "test",
    var age: Int,
)


data class BaseResp<T>(
    val code: Int,
    val msg: String,
    val data: T,
)
