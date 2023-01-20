package android.marc.com.storyapp.activity.addstory

import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.BaseResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel(){
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

    fun uploadImage(imageMultipart: MultipartBody.Part, description: RequestBody, latitude: Double?, longitude: Double?, auth: String) {
        _isLoading.value = true
        val uploadImageService = ApiConfig().getApiService().uploadImage(imageMultipart, description, latitude, longitude, auth)
        uploadImageService.enqueue(object : Callback<BaseResponse>{
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isSuccess.value = true
                } else {
                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun startLoading() {
        _isLoading.value = true
    }

    fun doneDialogIsSuccess() {
        _isSuccess.value = false
    }

    fun doneDialogIsError() {
        _isError.value = false
    }
}