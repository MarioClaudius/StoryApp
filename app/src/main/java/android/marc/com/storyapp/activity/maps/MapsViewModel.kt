package android.marc.com.storyapp.activity.maps

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.Story
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList

    fun getStoryWithLocationList(page: Int?, size: Int?, auth: String) {
        uiScope.launch {
            val response = ApiConfig().getApiService().getAllStories(page, size, 1, auth)
            _storyList.value = response.listStory
        }
    }
}