package com.georgedregan.movielist.data

import android.content.Context
import com.georgedregan.movielist.MovieListApplication

class SharedPreferencesManager {

    private val sharedPref =
        MovieListApplication.getAppContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveUsername(username: String) {
        sharedPref.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return sharedPref.getString("username", null)
    }

    fun clearData() {
        sharedPref.edit().clear().apply()
    }
}
