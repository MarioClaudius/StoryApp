package android.marc.com.storyapp.data

import android.marc.com.storyapp.api.ApiService
import android.marc.com.storyapp.database.StoryAppDatabase
import android.marc.com.storyapp.model.Story
import androidx.lifecycle.LiveData
import androidx.paging.*

class StoryRepository(private val database: StoryAppDatabase, private val apiService: ApiService, private val auth: String) {
    fun getAllStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                8
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, auth),
            pagingSourceFactory = {
                database.storyDao().getAllStories()
            }
        ).liveData
    }
}