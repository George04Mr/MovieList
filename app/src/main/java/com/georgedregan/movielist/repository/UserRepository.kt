package com.georgedregan.movielist.repository

import com.georgedregan.movielist.network.AuthApi
import com.georgedregan.movielist.network.LoginRequest
import com.georgedregan.movielist.network.LoginResponse
import com.georgedregan.movielist.network.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val authApi: AuthApi) {

    suspend fun login(username: String, password: String): LoginResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = authApi.login(LoginRequest(username, password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(username: String, password: String, isAdmin: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = authApi.register(RegisterRequest(username, password, isAdmin))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
