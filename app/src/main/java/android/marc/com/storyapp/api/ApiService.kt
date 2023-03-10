package android.marc.com.storyapp.api

import android.marc.com.storyapp.model.BaseResponse
import android.marc.com.storyapp.model.LoginResponse
import android.marc.com.storyapp.model.StoryListResponse
import android.marc.com.storyapp.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("password") password: String) : Call<BaseResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String, @Field("password") password: String) : Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(@Query("page") page: Int?, @Query("size") size: Int?, @Query("location") location: Int? = 0, @Header("Authorization") auth: String) : StoryListResponse

    @GET("stories/{id}")
    fun getStoryDetail(@Path("id") id: String, @Header("Authorization") auth: String) : Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part imageFile: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Double?,
        @Part("lon") longitude: Double?,
        @Header("Authorization") auth: String
    ) : Call<BaseResponse>
}