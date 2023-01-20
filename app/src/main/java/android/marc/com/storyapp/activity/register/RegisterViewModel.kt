package android.marc.com.storyapp.activity.register

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.BaseResponse
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

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

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val registerService = ApiConfig().getApiService().register(name, email, password)
        registerService.enqueue(object: Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isSuccess.value = true
                } else {
                    _isLoading.value = false
                    _isError.value = false
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = false
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