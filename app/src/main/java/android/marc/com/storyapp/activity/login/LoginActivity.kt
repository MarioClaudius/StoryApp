package android.marc.com.storyapp.activity.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.activity.register.RegisterActivity
import android.marc.com.storyapp.databinding.ActivityLoginBinding
import android.marc.com.storyapp.model.LoginSession
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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var session: LoginSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
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
                    binding.edLoginEmail.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.empty_password)
                }
                else -> {
                    loginViewModel.login(email, password)
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
            finish()
        }
    }

    private fun playAnimation() {
        val welcomeBackAnimation = ObjectAnimator.ofFloat(binding.tvWelcomeBack, View.ALPHA, 1f).setDuration(500)
        val emailEdtAnimation = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdtAnimation = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val loginBtnAnimation = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val dontHaveAccountTvAnimation = ObjectAnimator.ofFloat(binding.tvDontHaveAccount, View.ALPHA, 1f).setDuration(500)
        val signUpTvAnimation = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(dontHaveAccountTvAnimation, signUpTvAnimation)
        }

        AnimatorSet().apply {
            playSequentially(welcomeBackAnimation, emailEdtAnimation, passwordEdtAnimation, loginBtnAnimation, together)
            start()
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore), this, "")
        )[LoginViewModel::class.java]

        loginViewModel.getSession().observe(this) { session ->
            this.session = session
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        loginViewModel.isError.observe(this) { isError ->
            if (isError) {
                loginViewModel.doneDialogIsError()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.error_dialog_title))
                    setMessage(getString(R.string.error_dialog_login_message))
                    setPositiveButton(getString(R.string.error_dialog_button)){ _,_ -> }
                    create()
                    show()
                }
            }
        }

        loginViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                loginViewModel.doneDialogIsSuccess()
                val builder = AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success_dialog_title))
                    setMessage(getString(R.string.success_dialog_login_message))
                }
                val dialog = builder.create()
                dialog.show()
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        dialog.dismiss()
                        timer.cancel()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
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