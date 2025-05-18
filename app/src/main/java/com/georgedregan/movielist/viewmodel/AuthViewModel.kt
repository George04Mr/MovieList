package com.georgedregan.movielist.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.georgedregan.movielist.data.SharedPreferencesManager
import com.georgedregan.movielist.network.LoginResponse
import com.georgedregan.movielist.network.RetrofitClient
import com.georgedregan.movielist.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val repository = UserRepository(RetrofitClient.authApi)

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _currentUser = mutableStateOf<LoginResponse?>(null)
    val currentUser: State<LoginResponse?> = _currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    val sharedPreferencesManager = SharedPreferencesManager()

    init {
        _isLoggedIn.value = sharedPreferencesManager.getUsername() != null
    }

    fun login(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = repository.login(username.trim(), password)
                if (user != null) {
                    _isLoggedIn.value = true
                    _currentUser.value = user
                    _errorMessage.value = null

                    sharedPreferencesManager.saveUsername(username)
                } else {
                    _isLoggedIn.value = false
                    _errorMessage.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, password: String, isAdmin: Boolean = false) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.register(username, password, isAdmin)
                _errorMessage.value = null

                sharedPreferencesManager.saveUsername(username)
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        sharedPreferencesManager.clearData()
    }
}
