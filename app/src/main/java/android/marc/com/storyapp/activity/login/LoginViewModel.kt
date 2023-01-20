package android.marc.com.storyapp.activity.login

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.LoginResponse
import android.marc.com.storyapp.model.LoginSession
import android.marc.com.storyapp.model.SessionPreference
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: SessionPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _isError = MutableLiveData<Boolean>()
    val isError : LiveData<Boolean> = _isError

    init {
        _isError.value = false
        _isSuccess.value = false
    }

    fun getSession() : LiveData<LoginSession> {
        return pref.getSession().asLiveData()
    }

    fun saveSession(session: LoginSession) {
        viewModelScope.launch {
            pref.saveSession(session)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val loginService = ApiConfig().getApiService().login(email, password)
        loginService.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val isError = response.body()?.error
                    val session = response.body()?.loginSession
                    if (session != null && !isError!!) {
                        saveSession(LoginSession(session.userId, session.name, session.token))
                        _isLoading.value = false
                        _isSuccess.value = true
                    } else {
                        _isLoading.value = false
                        _isError.value = true
                    }
                } else {
                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun doneDialogIsSuccess() {
        _isSuccess.value = false
    }

    fun doneDialogIsError() {
        _isError.value = false
    }
}