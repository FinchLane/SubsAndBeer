package com.example.barbershop.data.network

import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.model.subscription.SubscriptionNotification
import com.example.barbershop.data.network.response.AuthRequest
import com.example.barbershop.data.network.response.AuthResponse
import com.example.barbershop.data.network.response.FamilyRequest
import com.example.barbershop.data.network.response.FamilyResponse
import com.example.barbershop.data.network.response.NotificationResponse
import com.example.barbershop.data.network.response.ProfileEditNameRequest
import com.example.barbershop.data.network.response.ProfileEditRequest
import com.example.barbershop.data.network.response.ProfileEditResponse
import com.example.barbershop.data.network.response.ProfileResponse
import com.example.barbershop.data.network.response.SecurityMessageResponse
import com.example.barbershop.data.network.response.SecurityResponse
import com.example.barbershop.data.network.response.SubscriptionDataResponse
import com.example.barbershop.data.network.response.SyncResponse
import com.example.barbershop.data.network.response.TemplatesResponse
import com.example.barbershop.data.network.response.TokenRequest
import com.example.barbershop.data.network.response.UploadPhotoResponse
import com.example.barbershop.data.network.response.VerifyCodeRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /** Запросы для авторизации (Authorization) */

    @POST("/send_sms")
    suspend fun sendSms(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("/verify_code")
    suspend fun verifyCode(@Body verifyCodeRequest: VerifyCodeRequest): Response<AuthResponse>

    /** Запросы для профиля (Profile) */

    @Authenticated
    @GET("/profile")
    suspend fun fetchProfile(): Response<ProfileResponse>

    @Authenticated
    @PUT("/profile/update_full_name")
    suspend fun editName(@Body profileEditRequest: ProfileEditNameRequest): Response<ProfileEditResponse>

    @Authenticated
    @PUT("/profile/update_email")
    suspend fun editEmail(@Body profileEditRequest: ProfileEditRequest): Response<ProfileEditResponse>

    @Authenticated
    @PUT("/profile/update_birthday")
    suspend fun editBirthday(@Body profileEditRequest: ProfileEditRequest): Response<ProfileEditResponse>

    @Authenticated
    @Multipart
    @POST("profile/update_photo")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): Response<UploadPhotoResponse>

    @Authenticated
    @DELETE("/profile/delete_account")
    suspend fun deleteAccount(): Response<ProfileEditResponse>

    @Authenticated
    @POST("/profile/logout")
    suspend fun logout(): Response<ProfileEditResponse>

    /** Запросы для загрузки авторизованных сессий (Security) */

    @Authenticated
    @GET("/session")
    suspend fun getSession(): Response<SecurityResponse>

    @Authenticated
    @DELETE("/session/delete/{id}")
    suspend fun deleteSession(@Path("id") id: String): Response<SecurityMessageResponse>

    @Authenticated
    @DELETE("/sessions/delete_all_except_current")
    suspend fun deleteAllSession(): Response<SecurityMessageResponse>

    /** Синхронизация данных с сервером (syncing) */

    @Authenticated
    @PUT("/addSubscription")
    suspend fun addSubscription(@Body subscriptions: Subscription): Response<SyncResponse>

    @Authenticated
    @PUT("/updateSubscription")
    suspend fun updateSubscription(@Body subscriptions: Subscription): Response<SyncResponse>

    @Authenticated
    @DELETE("/deleteSubscription/{id}")
    suspend fun deleteSubscription(@Path("id") id: String): Response<SyncResponse>

    @Authenticated
    @PUT("/addCategory")
    suspend fun addCategory(@Body categories: Category): Response<SyncResponse>

    @Authenticated
    @PUT("/updateCategory")
    suspend fun updateCategory(@Body categories: Category): Response<SyncResponse>

    @Authenticated
    @DELETE("/deleteCategory/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<SyncResponse>

    @Authenticated
    @PUT("/addPaymentMethod")
    suspend fun addPaymentMethod(@Body paymentMethods: PaymentMethod): Response<SyncResponse>

    @Authenticated
    @PUT("/updatePaymentMethod")
    suspend fun updatePaymentMethod(@Body paymentMethods: PaymentMethod): Response<SyncResponse>

    @Authenticated
    @DELETE("/deletePaymentMethod/{id}")
    suspend fun deletePaymentMethod(@Path("id") id: String): Response<SyncResponse>

    @Authenticated
    @PUT("/addSubscriptionNotification")
    suspend fun addSubscriptionNotification(subscriptionNotifications: SubscriptionNotification): Response<SyncResponse>

    @Authenticated
    @GET("/syncData")
    suspend fun getSyncData(@Query("lastSyncTime") lastSyncTime: Long): Response<SubscriptionDataResponse>

    @GET("/templates")
    suspend fun getTemplates(): Response<TemplatesResponse>

    @Authenticated
    @POST("/updateNotificationToken")
    suspend fun updateNotificationToken(@Body request: TokenRequest): Response<NotificationResponse>

    @Authenticated
    @PUT("/createFamily")
    suspend fun createFamily(@Body name: FamilyRequest): Response<FamilyResponse>
}