package android.marc.com.storyapp.activity.register

import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.databinding.ActivityRegisterBinding
import android.marc.com.storyapp.activity.login.LoginActivity
import android.marc.com.storyapp.model.UserModel
import android.marc.com.storyapp.model.UserPreference
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
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
                    registerViewModel.saveUser(UserModel(name, email, password, false))
                    Log.d("BTN REGISTER", "Name: $name, Email: $email, Password: $password")
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
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