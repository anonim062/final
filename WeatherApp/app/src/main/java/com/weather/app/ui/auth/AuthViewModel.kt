package com.weather.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weather.app.data.local.entity.UserEntity
import com.weather.app.data.repository.UserRepository
import com.weather.app.util.PreferencesManager
import com.weather.app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<UserEntity>>()
    val loginResult: LiveData<Resource<UserEntity>> = _loginResult

    private val _registerResult = MutableLiveData<Resource<Long>>()
    val registerResult: LiveData<Resource<Long>> = _registerResult

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = Resource.Error("Please fill all fields")
            return
        }

        _loginResult.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.login(username, password)
            if (result is Resource.Success) {
                // Save session
                result.data?.let {
                    PreferencesManager.currentUserId = it.id
                }
            }
            _loginResult.postValue(result)
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerResult.value = Resource.Error("Please fill all fields")
            return
        }

        if (password != confirmPassword) {
            _registerResult.value = Resource.Error("Passwords do not match")
            return
        }

        _registerResult.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.register(username, email, password)
            if (result is Resource.Success) {
                // Auto login after registration
                result.data?.let { userId ->
                    PreferencesManager.currentUserId = userId
                }
            }
            _registerResult.postValue(result)
        }
    }
}

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
