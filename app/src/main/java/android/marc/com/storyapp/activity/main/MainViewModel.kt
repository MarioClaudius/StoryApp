package android.marc.com.storyapp.activity.main

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import android.marc.com.storyapp.model.Story
import android.marc.com.storyapp.model.StoryListResponse
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SessionPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList

    fun displayStoryList(page: Int?, size: Int?, location: Int?, auth: String) {
        _isLoading.value = true
        val getAllStoriesListService = ApiConfig().getApiService().getAllStories(page, size, location, auth)
        getAllStoriesListService.enqueue(object : Callback<StoryListResponse>{
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _storyList.value = response.body()?.listStory
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                _isLoading.value = false
            }

        })
    }

    fun getSession(): LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}