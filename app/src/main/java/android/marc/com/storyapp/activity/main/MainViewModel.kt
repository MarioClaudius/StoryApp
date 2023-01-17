package android.marc.com.storyapp.activity.main

import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SessionPreference): ViewModel() {
    fun getSession(): LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}