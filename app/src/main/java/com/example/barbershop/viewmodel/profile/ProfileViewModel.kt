package com.example.barbershop.viewmodel.profile

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.AppSettings
import com.example.barbershop.Constants.BASE_URl
import com.example.barbershop.data.model.MessageEntity
import com.example.barbershop.data.model.ProfileEntity
import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.family.FamilyMemberEntity
import com.example.barbershop.data.network.response.FamilyRequest
import com.example.barbershop.data.repository.ProfileRepository
import com.example.barbershop.utils.jwtToken.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val tokenManager: EncryptedTokenManager,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> = _profileImage.asStateFlow()

    val messages: StateFlow<List<MessageEntity>> =
        repository.getAllMessage().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val families: StateFlow<List<FamilyEntity>> =
        repository.getAllFamily().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val familyMembers: StateFlow<List<FamilyMemberEntity>> =
        repository.getFamilyMembers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val isAuth = appSettings.isAuthorizedFlow

    var phoneNumberUser by mutableStateOf("")
        private set

    var firstNameUser by mutableStateOf("")
        private set

    var lastNameUser by mutableStateOf("")
        private set

    var middleNameUser by mutableStateOf("")
        private set

    var emailUser by mutableStateOf("")
        private set

    var birthdayUser by mutableStateOf("")
        private set

    var familyName by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    var showConfirmationMessageDeleteAccount by mutableStateOf(false)
        private set

    var isProfileLoaded by mutableStateOf(false)
        private set

    init {
        loadProfileFromCache()
    }

    private fun loadProfileFromCache() {
        viewModelScope.launch {
            val cachedProfile = repository.getProfileFromCache()
            if (cachedProfile != null && !isProfileLoaded){
                phoneNumberUser = cachedProfile.phoneNumber
                firstNameUser = cachedProfile.firstName
                lastNameUser = cachedProfile.lastName
                middleNameUser = cachedProfile.middleName
                emailUser = cachedProfile.email
                birthdayUser = cachedProfile.birthday
                setProfileImage(BASE_URl + cachedProfile.photoPath.toString())
                isProfileLoaded = true
            }
            else{
                fetchProfileFromServer()
            }
        }
    }

    fun showConfirmationMessage(boolean: Boolean){
        showConfirmationMessageDeleteAccount = boolean
    }

    private fun saveProfileToCache(profile: ProfileEntity){
        viewModelScope.launch {
            repository.saveProfileToCache(profile)
        }
    }

    fun updateBirthday(newDate: String){
        birthdayUser = newDate
        editBirthday(newDate)
    }

    fun updateFamilyName(name: String) {
        familyName = name
    }

    private fun clearProfile(){
        viewModelScope.launch {
            repository.clearProfileCache()
        }
    }

    fun setProfileImage(image: String) {
        _profileImage.value = image
    }

    fun fetchProfileFromServer(){
        viewModelScope.launch {
            try {
                val response = repository.fetchProfile()
                handleResponse(response){
                    val responseBody = response.body()
                    responseBody?.let {
                        val profile = responseBody.toProfileEntity()
                        saveProfileToCache(profile)

                        phoneNumberUser = profile.phoneNumber
                        firstNameUser = profile.firstName
                        lastNameUser = profile.lastName
                        middleNameUser = profile.middleName
                        emailUser = profile.email
                        birthdayUser = profile.birthday
                        _profileImage.value = BASE_URl + profile.photoPath
                        isProfileLoaded = true
                    }
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    fun editName(firstName: String?, lastName: String?, middleName: String?){
        viewModelScope.launch {
            if (firstName.isNullOrEmpty()) {
                errorMessage = "Имя не может быть пустым"
                return@launch
            }
            try {
                val response = repository.editName(firstName, lastName.toString(), middleName.toString())
                handleResponse(response){
                    val responseBody = response.body()
                    responseBody?.let {
                        firstNameUser = firstName
                        lastNameUser = lastName ?: ""
                        middleNameUser = middleName ?: ""

                        saveProfileToCache(
                            ProfileEntity(
                                phoneNumber = phoneNumberUser,
                                firstName = firstNameUser,
                                lastName = lastNameUser,
                                middleName = middleNameUser,
                                email = emailUser,
                                birthday = birthdayUser
                            )
                        )
                        // нужно добавить уведомление об успехе изменения
                        _navigateTo.value = "editProfile"
                    }
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    fun editEmail(email: String){
        viewModelScope.launch {
            if (email.isEmpty()){
                errorMessage = "Email не может быть пустым"
                return@launch
            }
            try {
                val response = repository.editEmail(email)
                handleResponse(response){
                    emailUser = email
                    val responseBody = response.body()
                    responseBody.let {
                        saveProfileToCache(
                            ProfileEntity(
                                phoneNumber = phoneNumberUser,
                                firstName = firstNameUser,
                                lastName = lastNameUser,
                                middleName = middleNameUser,
                                email = emailUser,
                                birthday = birthdayUser
                            )
                        )
                        // нужно добавить уведомление об успехе изменения
                        _navigateTo.value = "editProfile"
                    }
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    private fun editBirthday(birthday: String){
        viewModelScope.launch {
            try {
                val response = repository.editBirthday(birthday)
                handleResponse(response){
                    birthdayUser = birthday
                    saveProfileToCache(
                        ProfileEntity(
                            phoneNumber = phoneNumberUser,
                            firstName = firstNameUser,
                            lastName = lastNameUser,
                            middleName = middleNameUser,
                            email = emailUser,
                            birthday = birthdayUser
                        )
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    fun logout(){
        tokenManager.deleteToken()
        _navigateTo.value = "login"
        viewModelScope.launch {
            appSettings.setAuthorized(false)
            try {
                val response = repository.logout()
                handleResponse(response){
                    tokenManager.deleteToken()
                    _navigateTo.value = "home"
                    clearProfile()
                }
            }
            catch (e: Exception){
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    fun deleteAccount(){
        viewModelScope.launch {
            try {
                val response = repository.deleteAccount()
                handleResponse(response){
                    _navigateTo.value = "login"
                    // нужно добавить уведомление об успехе
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка при отправке запроса: ${e.message}"
                Timber.tag("API").e("Error confirming code: ${e.message}")
            }
        }
    }

    fun uploadPhoto(context: Context, uri: Uri) = viewModelScope.launch {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri) ?: "image/*"

        val fileName = resolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null
        )?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(idx)
        } ?: "upload.jpg"

        val inputStream = resolver.openInputStream(uri)
        if (inputStream == null) {
            errorMessage = "Не удалось открыть файл"
            return@launch
        }
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

        // 4. Создаём MultipartBody.Part
        val imagePart = MultipartBody.Part.createFormData(
            "photo",
            fileName,
            requestBody
        )

        try {
            val response = repository.uploadPhoto(part = imagePart)

            if (response.isSuccessful) {
                val serverPath = response.body()?.pictureUrl
                val fullUrl = if (serverPath.isNullOrBlank()) {
                    uri.toString()
                } else {
                    BASE_URl.trimEnd('/') + "/" + serverPath.removePrefix("/")
                }

                _profileImage.value = fullUrl

                saveProfileToCache(
                    ProfileEntity(
                        phoneNumber = phoneNumberUser,
                        firstName   = firstNameUser,
                        lastName    = lastNameUser,
                        middleName  = middleNameUser,
                        email       = emailUser,
                        birthday    = birthdayUser,
                        photoPath   = fullUrl
                    )
                )

                Timber.tag("ProfileVM").i("Фото успешно загружено: $fullUrl")
            } else {
                val fallback = uri.toString()
                _profileImage.value = fallback

                saveProfileToCache(
                    ProfileEntity(
                        phoneNumber = phoneNumberUser,
                        firstName   = firstNameUser,
                        lastName    = lastNameUser,
                        middleName  = middleNameUser,
                        email       = emailUser,
                        birthday    = birthdayUser,
                        photoPath   = fallback
                    )
                )

                errorMessage = "Ошибка ${response.code()}: ${response.errorBody()?.string()}"
                Timber.tag("ProfileVM").e("uploadPhoto failed: ${response.code()}")
            }
        } catch (e: Exception) {
            val fallback = uri.toString()
            _profileImage.value = fallback

            saveProfileToCache(
                ProfileEntity(
                    phoneNumber = phoneNumberUser,
                    firstName   = firstNameUser,
                    lastName    = lastNameUser,
                    middleName  = middleNameUser,
                    email       = emailUser,
                    birthday    = birthdayUser,
                    photoPath   = fallback
                )
            )

            errorMessage = "Ошибка сети: ${e.localizedMessage}"
            Timber.tag("ProfileVM").e(e, "uploadPhoto exception")
        }
    }

    fun deleteMessage(id: Int) {
        viewModelScope.launch {
            repository.clearMessage(id)
        }
    }

    fun deleteAllMessage() {
        viewModelScope.launch {
            repository.clearAllMessage()
        }
    }

    fun checkAllMessages() {
        viewModelScope.launch {
            repository.checkAllMessages()
        }
    }

    fun insertMessage(message: MessageEntity) {
        viewModelScope.launch {
            repository.insertMessage(message)
        }
    }

    fun createFamily() {
        viewModelScope.launch {

            val response = repository.createFamily(FamilyRequest(
                name = familyName.ifEmpty { "Семья №${families.value.count() + 1}" }
            ))
            if (response.isSuccessful){
                val responseBody = response.body()
                repository.insertFamily(
                    FamilyEntity(
                        id = responseBody?.id ?: 0,
                        name = responseBody?.name ?: "Семья № ${families.value.count()}",
                        createdAt = responseBody?.createdAt ?: 0L
                    )
                )
                familyName = ""
            }
            else {
                handleResponse(response) {

                }
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
                Timber.tag("API").e("Error 401: Unauthorized")
            }
            response.code() == 404 -> {
                errorMessage = "Данные не найдены."
                Timber.tag("API").e("Error 404: Data Not Found")
            }
            response.code() == 500 -> {
                errorMessage = "Ошибка сервера. Попробуйте позже."
                Timber.tag("API").e("Error 500: Server Error")
            }
            else -> {
                errorMessage = "Ошибка: ${response.code()}"
                Timber.tag("API").e("Error: ${response.code()}")
            }
        }
    }

    fun clearNavigationEvent() {
        _navigateTo.value = null
    }
}