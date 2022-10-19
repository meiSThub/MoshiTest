package com.mei.moshitest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/**
 * Created by mei on 2022/10/19.
 * Description:
 */
class MoshiDefaultAdapterFactory {
    companion object {
        val FACTORY = object : JsonAdapter.Factory {
            override fun create(
                type: Type,
                annotations: MutableSet<out Annotation>,
                moshi: Moshi
            ): JsonAdapter<*>? {
                if (annotations.isNotEmpty()) return null

                if (type == String::class.java) return STRING_JSON_ADAPTER

                return null
            }
        }

        val STRING_JSON_ADAPTER = object : JsonAdapter<String>() {
            override fun fromJson(reader: JsonReader): String {
                // 替换null为""
                if (reader.peek() != JsonReader.Token.NULL) {
                    return reader.nextString()
                }
                reader.nextNull<String>()
                return ""
            }

            override fun toJson(writer: JsonWriter, value: String?) {
                writer.value(value)
            }
        }
    }
}