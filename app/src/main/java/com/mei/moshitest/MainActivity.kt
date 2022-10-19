package com.mei.moshitest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.rawType
import java.lang.reflect.ParameterizedType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test1()
        testList()
        testT()
    }

    fun test1() {
        /*创建moshi*/
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())// 使用kotlin反射处理，要加上这个
            .addLast(MoshiDefaultAdapterFactory.FACTORY)
            .build()
        /*声明adapter，指定要处理的类型*/
        val jsonAdapter = moshi.adapter(User::class.java)
        val jsonStr = """
        {"name":null,"age":18}
    """.trimIndent()
        val user = jsonAdapter.fromJson(jsonStr)
        println("user = $user")
        Log.i("MainActivity", "user=$user")
        Log.i("MainActivity", "user=${jsonAdapter.toJson(user)}")
    }

    fun testList() {
        /*创建moshi*/
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
            .build()

        val users = mutableListOf<User>()
        (1..3).forEach {
            val user = User("喻志强", 25 + it)
            users.add(user)
        }
        /*声明adapter，指定要处理的类型*/
        val parameterizedType = Types.newParameterizedType(List::class.java, User::class.java)
        val jsonAdapter = moshi.adapter<List<User>>(parameterizedType)
        val toJson = jsonAdapter.toJson(users)
        println("toJson = ${toJson}")
        val jsonStr = """
        [{"name":"喻志强","age":26},{"name":"喻志强","age":27},{"name":"喻志强","age":28}]
    """.trimIndent()
        val fromJson = jsonAdapter.fromJson(jsonStr)
        println("fromJson = ${fromJson}")
        if (fromJson != null) {
            fromJson.forEach {
                println("it.age = ${it.age}")
            }
        }
    }

    fun testT() {
        /*创建moshi*/
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
            .build()
        val user = User("喻志强", 28)
        val baseResp = BaseResp(200, "成功", user)
        /*声明adapter，指定要处理的类型*/
        val type = object : TypeToken<BaseResp<User>>() {}::class.java.genericSuperclass
            .let { it as ParameterizedType }
            .actualTypeArguments
            .first()
        val jsonAdapter = moshi.adapter<BaseResp<User>>(type)
        val toJson = jsonAdapter.toJson(baseResp)
        println("toJson = ${toJson}")
    }

//    inline fun <reified T> getGenericType(): Type {
//        return object : MoshiTypeReference<T>() {}::class.java
//            .genericSuperclass
//            .let { it as ParameterizedType }
//            .actualTypeArguments
//            .first()
//    }
}

abstract class TypeToken<T>