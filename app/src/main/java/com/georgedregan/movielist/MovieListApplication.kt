package com.georgedregan.movielist

import android.app.Application
import android.content.Context

class MovieListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MovieListApplication

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}