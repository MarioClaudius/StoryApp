package android.marc.com.storyapp.activity.login

import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.databinding.ActivityLoginBinding
import android.marc.com.storyapp.activity.register.RegisterActivity
import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.model.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var session: LoginSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        hideActionBar()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Email is empty"
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Password is empty"
                }
                else -> {
                    val loginService = ApiConfig().getApiService().login(email, password)
                    loginService.enqueue(object: Callback<LoginResponse>{
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val isError = response.body()?.error
                                val session = response.body()?.loginSession
                                if (session != null && !isError!!) {
                                    loginViewModel.login(LoginSession(session.userId, session.name, session.token))
                                    Log.d("BTN LOGIN", "Email: $email, Password: $password")
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Session failed to be saved", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getSession().observe(this) { session ->
            this.session = session
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