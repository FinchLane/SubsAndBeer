package com.example.barbershop.data.network.response

import com.example.barbershop.data.model.ProfileEntity
import com.google.gson.annotations.SerializedName

data class ProfileEditRequest(val data: String)

data class ProfileEditNameRequest(val firstName: String?, val lastName: String?, val middleName: String?)

data class ProfileResponse(
    val message: String,
    val id: String? = null,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val email: String? = null,
    val birthday: String? = null,
    val profilePicture: String? = null
) {
    fun toProfileEntity(): ProfileEntity {
        return ProfileEntity(
            phoneNumber = phoneNumber ?: "",
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            middleName = middleName ?: "",
            email = email ?: "",
            birthday = birthday ?: "",
            photoPath = profilePicture
        )
    }
}

data class UploadPhotoResponse(
    @SerializedName("picture_url")
    val pictureUrl: String
)

data class ProfileEditResponse(val message: String)