package android.marc.com.storyapp.activity.maps

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.Story
import android.marc.com.storyapp.model.StoryListResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList

    fun getStoryWithLocationList(page: Int?, size: Int?, auth: String) {
        _isLoading.value = true
        uiScope.launch {
            val response = ApiConfig().getApiService().getAllStories(page, size, 1, auth)
            _storyList.value = response.listStory
        }
//        getAllStoriesListService.enqueue(object : Callback<StoryListResponse> {
//            override fun onResponse(
//                call: Call<StoryListResponse>,
//                response: Response<StoryListResponse>
//            ) {
//                if (response.isSuccessful) {
//                    _isLoading.value = false
//                    _storyList.value = response.body()?.listStory
//                }
//            }
//
//            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
//                _isLoading.value = false
//            }
//
//        })
    }
}