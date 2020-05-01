package com.vmenon.mpo.persistence.room.base

import androidx.room.TypeConverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

class Converters {
    private val gson: Gson = Gson()
    private val stringListType: Type =
        object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromJsonString(value: String): List<String> {
        return gson.fromJson(value, stringListType)
    }

    @TypeConverter
    fun toJsonString(values: List<String>): String {
        return gson.toJson(values)
    }
}
