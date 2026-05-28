package com.example.barbershop.viewmodel.security

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.data.network.response.UserSession
import com.example.barbershop.data.repository.SecurityRepository
import com.example.barbershop.utils.jwtToken.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val tokenManager: EncryptedTokenManager,
    private val repository: SecurityRepository
): ViewModel() {
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    var currentSession by mutableStateOf<UserSession?>(null)
    var otherSessions by mutableStateOf(listOf<UserSession>())

    var errorMessage by mutableStateOf("")
        private set

    init {
        session()
    }

    fun session(){
        viewModelScope.launch {
            try {
                val response = repository.getSession()
                handleResponse(response){
                    val responseBody = response.body()
                    responseBody?.let {
                        val sessions = it.sessions
                        currentSession = sessions.find { session -> session.isCurrent }
                        otherSessions = sessions.filterNot { session -> session.isCurrent }
                    }
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Log.e("API", "Error confirming code: ${e.message}")
            }
        }
    }

    fun deleteSession(id: String){
        viewModelScope.launch {
            try {
                val response = repository.deleteSession(id)
                handleResponse(response){
                    otherSessions = otherSessions.filter { it.id != id }
                    _navigateTo.value = "authDevices"
                    // Добавить уведомление
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Log.e("API", "Error confirming code: ${e.message}")
            }
        }
    }

    fun deleteAllSession(){
        viewModelScope.launch {
            try {
                val response = repository.deleteAllSession()
                handleResponse(response){
                    otherSessions = listOf()
                    // нужно добавить уведомление о выходе из всех сессий
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Log.e("API", "Error confirming code: ${e.message}")
            }
        }
    }

    private fun handleResponse(response: Response<*>, onSuccess: () -> Unit) {
        when {
            response.isSuccessful -> {
                onSuccess()
            }
            response.code() == 401 -> {
                tokenManager.deleteToken()
                _navigateTo.value = "login"
                Log.e("API", "Error 401: Unauthorized")
            }
            response.code() == 404 -> {
                errorMessage = "Данные не найдены."
                Log.e("API", "Error 404: Data Not Found")
            }
            response.code() == 500 -> {
                errorMessage = "Ошибка сервера. Попробуйте позже."
                Log.e("API", "Error 500: Server Error")
            }
            else -> {
                errorMessage = "Ошибка: ${response.code()}"
                Log.e("API", "Error: ${response.code()}")
            }
        }
    }

    fun clearNavigationEvent() {
        _navigateTo.value = null
    }
}