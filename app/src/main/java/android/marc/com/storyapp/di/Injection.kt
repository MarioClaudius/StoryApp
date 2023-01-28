package android.marc.com.storyapp.di

import android.content.Context
import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.data.StoryRepository
import android.marc.com.storyapp.database.StoryAppDatabase

object Injection {
    fun provideRepository(context: Context, auth: String): StoryRepository {
        val database = StoryAppDatabase.getDatabase(context)
        val apiService = ApiConfig().getApiService()
        return StoryRepository(database, apiService, auth)
    }
}