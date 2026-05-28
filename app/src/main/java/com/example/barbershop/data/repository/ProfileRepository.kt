package com.example.barbershop.data.repository

import com.example.barbershop.data.database.MessageDao
import com.example.barbershop.data.database.ProfileDao
import com.example.barbershop.data.database.family.FamilyDao
import com.example.barbershop.data.model.MessageEntity
import com.example.barbershop.data.model.ProfileEntity
import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.family.FamilyMemberEntity
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.network.response.FamilyRequest
import com.example.barbershop.data.network.response.FamilyResponse
import com.example.barbershop.data.network.response.ProfileEditNameRequest
import com.example.barbershop.data.network.response.ProfileEditRequest
import com.example.barbershop.data.network.response.ProfileEditResponse
import com.example.barbershop.data.network.response.ProfileResponse
import com.example.barbershop.data.network.response.UploadPhotoResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response

class ProfileRepository(
    private val apiService: ApiService,
    private val profileDao: ProfileDao,
    private val messageDao: MessageDao,
    private val familyDao: FamilyDao
) {

    suspend fun fetchProfile(): Response<ProfileResponse> {
        return apiService.fetchProfile()
    }

    suspend fun editName(firstName: String, lastName: String, middleName: String): Response<ProfileEditResponse>{
        return apiService.editName(ProfileEditNameRequest(firstName, lastName, middleName))
    }

    suspend fun editEmail(email: String): Response<ProfileEditResponse>{
        return apiService.editEmail(ProfileEditRequest(email))
    }

    suspend fun editBirthday(birthday: String): Response<ProfileEditResponse>{
        return apiService.editBirthday(ProfileEditRequest(birthday))
    }

    suspend fun uploadPhoto(part: MultipartBody.Part): Response<UploadPhotoResponse> {
        return apiService.uploadPhoto(part)
    }

    suspend fun deleteAccount(): Response<ProfileEditResponse>{
        return apiService.deleteAccount()
    }

    suspend fun logout():Response<ProfileEditResponse>{
        return apiService.logout()
    }

    suspend fun saveProfileToCache(profile: ProfileEntity){
        profileDao.insertProfile(profile)
    }

    suspend fun getProfileFromCache(): ProfileEntity? {
        return profileDao.getProfile()
    }

    suspend fun clearProfileCache(){
        profileDao.clearProfile()
    }

    suspend fun insertMessage(message: MessageEntity) = messageDao.insertMessage(message)

    suspend fun clearAllMessage() = messageDao.clearAllMessages()

    suspend fun clearMessage(id: Int) = messageDao.deleteMessage(id)

    fun getAllMessage(): Flow<List<MessageEntity>> = messageDao.getAllMessages()

    suspend fun checkAllMessages() = messageDao.checkAllMessages()

    fun getNewMessage(): Flow<List<MessageEntity>> = messageDao.getNewMessages()

    /** Семья */

    suspend fun insertFamily(familyEntity: FamilyEntity) = familyDao.insertFamily(familyEntity)

    fun getAllFamily(): Flow<List<FamilyEntity>> = familyDao.getAllFamily()

    fun getFamily(id: Int): Flow<FamilyEntity> = familyDao.getFamily(id)

    fun getFamilyMembers(): Flow<List<FamilyMemberEntity>> = familyDao.getFamilyMembers()

    suspend fun createFamily(name: FamilyRequest): Response<FamilyResponse> {
        return apiService.createFamily(name)
    }
}