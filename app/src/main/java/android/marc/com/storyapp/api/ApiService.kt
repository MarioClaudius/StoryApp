package android.marc.com.storyapp.api

import android.marc.com.storyapp.model.BaseResponse
import android.marc.com.storyapp.model.LoginResponse
import android.marc.com.storyapp.model.StoryListResponse
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
    fun getAllStories(@Query("page") page: Int?, @Query("size") size: Int?, @Query("location") location: Int? = 0, @Header("Authorization") auth: String) : Call<StoryListResponse>
}