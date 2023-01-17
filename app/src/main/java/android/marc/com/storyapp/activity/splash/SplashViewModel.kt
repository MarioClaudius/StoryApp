package android.marc.com.storyapp.activity.splash

import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class SplashViewModel(private val pref: SessionPreference): ViewModel() {
    fun getSession(): LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }
}