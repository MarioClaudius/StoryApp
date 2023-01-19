package android.marc.com.storyapp.activity.storydetail

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.SessionPreference
import android.marc.com.storyapp.model.Story
import android.marc.com.storyapp.model.StoryResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryDetailViewModel(private val pref: SessionPreference): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail : LiveData<Story> = _storyDetail

    fun getStoryDetail(storyId: String, auth: String) {
        _isLoading.value = true
        val getStoryDetailService = ApiConfig().getApiService().getStoryDetail(storyId, auth)
        getStoryDetailService.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful){
                    _isLoading.value = false
                    _storyDetail.value = response.body()?.story
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}