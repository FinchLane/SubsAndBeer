package com.example.barbershop.viewmodel.authorization

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.AppSettings
import com.example.barbershop.data.network.response.TokenRequest
import com.example.barbershop.data.repository.AuthRepository
import com.example.barbershop.data.repository.SubRepository
import com.example.barbershop.utils.jwtToken.EncryptedTokenManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val repository: AuthRepository,
    private val subRepository: SubRepository,
    private val tokenManager: EncryptedTokenManager,
    private val appSettings: AppSettings,
    private val ipAddress: String,
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    var phoneNumber by mutableStateOf("")
        private set

    var code by mutableStateOf("")
        private set

    private var isLoginSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    val isAuthorized = appSettings.isAuthorizedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun onPhoneNumberChange(newPhoneNumber: String) {
        phoneNumber = newPhoneNumber.filter { it.isDigit() }.take(10)
    }

    fun onCodeChange(newCode: String) {
        code = newCode.filter { it.isDigit() }.take(4)
    }

    fun clearErrorMessage(){
        errorMessage = ""
    }

    fun sendSms(ipAddress: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.sendSms("7$phoneNumber", ipAddress)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        Timber.tag("API").d("The code has been sent successfully")
                        errorMessage = ""
                        _navigateTo.value = "confirmation?phoneNumber=$phoneNumber"
                    }
                } else {
                    handleErrorResponse(response.code())
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error sending sms: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyCode() {
        isLoading = true
        val deviceName = Build.MODEL
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName ?: "unknown"

        viewModelScope.launch {
            try {
                val response = repository.verifyCode("7$phoneNumber", code, deviceName, versionName, ipAddress)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        isLoginSuccessful = true
                        responseBody.accessToken?.let { token ->
                            tokenManager.saveToken(token)
                            appSettings.setAuthorized(true)
                            Timber.tag("API").d("Token saved: $token")
                            _navigateTo.value = "home"
                        }
                    }
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            val fcmToken = task.result
                            viewModelScope.launch {
                                try {
                                    val response = subRepository.updateNotificationToken(
                                        TokenRequest(fcmToken))
                                    if (response.isSuccessful) {
                                        Timber.tag("FCM_TOKEN").d("Токен успешно отправлен на сервер")
                                    }
                                    else {
                                        Timber.tag("FCM_TOKEN").e("Ошибка при отправке токена: ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    Timber.tag("FCM_TOKEN").e("Ошибка при обновлении FCM токена: ${e.message}")
                                }
                            }
                        }
                        else {
                            Timber.tag("FCM_TOKEN").e("Не удалось получить FCM токен: ${task.exception?.message}")
                        }
                    } )
                } else {
                    handleErrorResponse(response.code())
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun setUnauthorizedAccess() {
        viewModelScope.launch {
            appSettings.setAuthorized(false)
            _navigateTo.value = "home"
        }
    }

    private fun handleErrorResponse(code: Int) {
        when (code) {
            400 -> {
                errorMessage = "Неверный запрос: проверьте введённый номер или код."
                Timber.tag("API").e("Error 400: Bad Request")
            }
            500 -> {
                errorMessage = "Ошибка сервера: попробуйте позже."
                Timber.tag("API").e("Error 500: Server Error")
            }
            else -> {
                errorMessage = "Ошибка: код $code"
                Timber.tag("API").e("Error with code: $code")
            }
        }
    }

    fun clearNavigationEvent() {
        _navigateTo.value = null
    }
}