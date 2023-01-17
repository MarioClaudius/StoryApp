package android.marc.com.storyapp.activity.register

import android.content.Intent
import android.marc.com.storyapp.activity.login.LoginActivity
import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.databinding.ActivityRegisterBinding
import android.marc.com.storyapp.model.BaseResponse
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideActionBar()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = "Name is empty"
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = "Email is empty"
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = "Password is empty"
                }
                else -> {
                    val registerService = ApiConfig().getApiService().register(name, email, password)
                    registerService.enqueue(object: Callback<BaseResponse>{
                        override fun onResponse(
                            call: Call<BaseResponse>,
                            response: Response<BaseResponse>
                        ) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                Log.d("BTN REGISTER", "Name: $name, Email: $email, Password: $password")
                                Toast.makeText(this@RegisterActivity, body?.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Register failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun hideActionBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}