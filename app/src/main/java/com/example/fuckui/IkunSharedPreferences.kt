package com.example.fuckui

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


object IkunKey {
    const val IKUN_IMAGE = "ikun_image"
    const val IKUN_IMAGE_WIDTH = "ikun_image_width"
    const val IKUN_IMAGE_HEIGHT = "ikun_image_height"
}

class IkunSharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE)
    }

    companion object {
        private const val SP_NAME = "ikun_sp"
        fun newInstance(context: Context): IkunSharedPreferences {
            return IkunSharedPreferences(context)
        }
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }
}