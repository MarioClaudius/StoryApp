package android.marc.com.storyapp.activity.splash

import android.marc.com.storyapp.model.UserModel
import android.marc.com.storyapp.model.UserPreference
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class SplashViewModel(private val pref: UserPreference): ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}