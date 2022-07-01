package com.instaconnect.android.utils

import android.app.Activity
import android.content.Context
import com.instaconnect.android.utils.models.User
import gun0912.tedimagepicker.util.ToastUtil.context

class Prefrences {

    companion object{

        fun getUser(): User? {

            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            return CommonUtil.instance.fromJson(
                sharedPreferences.getString(
                    "User",
                    ""
                ),
                User::class.java
            )
        }

        fun setUser(user: User?) {
            val userJson: String = CommonUtil.instance.toJson(user)
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(
                "User",
                userJson
            ).apply()
        }

        fun savePreferencesString(context: Context, key: String, value: String): String {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()

            return key
        }

        fun getPreferences(context: Context, key: String): String? {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, "")
        }

        fun getBooleanPreferences(context: Context, key: String): Boolean {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(key, false)
        }

        fun savePreferencesBoolean(context: Context, key: String, value: Boolean): String {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
            return key
        }

        fun removePreferences(context: Activity, key: String) {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(key)
            editor.apply()
        }

        fun clearPreferences(context: Context) {
            val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        }

    }
}