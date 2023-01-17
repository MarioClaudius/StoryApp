package android.marc.com.storyapp.activity.register

import android.marc.com.storyapp.model.UserModel
import android.marc.com.storyapp.model.UserPreference
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(private val pref: UserPreference): ViewModel() {
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}