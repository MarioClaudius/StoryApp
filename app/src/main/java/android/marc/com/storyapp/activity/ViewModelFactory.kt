package android.marc.com.storyapp.activity

import android.content.Context
import android.marc.com.storyapp.activity.addstory.AddStoryViewModel
import android.marc.com.storyapp.activity.login.LoginViewModel
import android.marc.com.storyapp.activity.main.MainViewModel
import android.marc.com.storyapp.activity.maps.MapsViewModel
import android.marc.com.storyapp.activity.register.RegisterViewModel
import android.marc.com.storyapp.activity.splash.SplashViewModel
import android.marc.com.storyapp.activity.storydetail.StoryDetailViewModel
import android.marc.com.storyapp.di.Injection
import android.marc.com.storyapp.model.SessionPreference
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val pref: SessionPreference,
    private val context: Context,
    private val auth: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(context, auth)) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(pref) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel() as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}