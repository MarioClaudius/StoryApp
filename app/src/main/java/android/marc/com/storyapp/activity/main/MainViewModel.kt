package android.marc.com.storyapp.activity.main

import android.marc.com.storyapp.data.StoryRepository
import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import android.marc.com.storyapp.model.Story
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SessionPreference, storyRepository: StoryRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val story: LiveData<PagingData<Story>> = storyRepository.getAllStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}