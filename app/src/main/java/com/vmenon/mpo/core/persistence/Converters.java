package com.vmenon.mpo.core.persistence;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    static final Gson gson;
    static final Type stringListType;
    static {
        gson = new Gson();
        stringListType = new TypeToken<List<String>>(){}.getType();
    }
    @TypeConverter
    public static List<String> fromJsonString(String value) {
        return gson.fromJson(value, stringListType);
    }

    @TypeConverter
    public static String toJsonString(List<String> values) {
        return gson.toJson(values);
    }
}
