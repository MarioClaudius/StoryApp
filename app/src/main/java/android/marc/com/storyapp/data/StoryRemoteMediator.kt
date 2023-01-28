package android.marc.com.storyapp.data

import android.marc.com.storyapp.api.ApiService
import android.marc.com.storyapp.database.StoryAppDatabase
import android.marc.com.storyapp.model.Story
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator (
    private val database: StoryAppDatabase,
    private val apiService: ApiService,
    private val auth: String
) : RemoteMediator<Int, Story>(){

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = INITIAL_PAGE_INDEX

        try {
            val response = apiService.getAllStories(page, state.config.pageSize, null, auth)
            val storyList = response.listStory
            Log.d("REMOTEMEDIATOR", storyList.toString())
            val endOfPaginationReached = storyList.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().deleteAllStories()
                }
                database.storyDao().insertStory(storyList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}