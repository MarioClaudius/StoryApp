package android.marc.com.storyapp.activity.login

import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: SessionPreference) : ViewModel() {
    fun getSession() : LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }

    fun login(session: LoginSession) {
        viewModelScope.launch {
            pref.login(session)
        }
    }
}