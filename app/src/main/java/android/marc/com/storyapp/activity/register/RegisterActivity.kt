package android.marc.com.storyapp.activity.register

import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.activity.login.LoginActivity
import android.marc.com.storyapp.databinding.ActivityRegisterBinding
import android.marc.com.storyapp.model.SessionPreference
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideActionBar()
        setupViewModel()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.empty_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.empty_password)
                }
                else -> {
                    registerViewModel.register(name, email, password)
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore), this, "")
        )[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        registerViewModel.isError.observe(this) { isError ->
            if (isError) {
                registerViewModel.doneDialogIsError()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.error_dialog_title))
                    setMessage(getString(R.string.error_dialog_register_message))
                    setPositiveButton(getString(R.string.error_dialog_button)){ _,_ -> }
                    create()
                    show()
                }
            }
        }

        registerViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                registerViewModel.doneDialogIsSuccess()
                val builder = AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success_dialog_title))
                    setMessage(getString(R.string.success_dialog_register_message))
                }
                val dialog = builder.create()
                dialog.show()
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        dialog.dismiss()
                        timer.cancel()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 1000)
            }
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