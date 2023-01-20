package android.marc.com.storyapp.activity.splash

import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.activity.login.LoginActivity
import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.databinding.ActivitySplashBinding
import android.marc.com.storyapp.model.SessionPreference
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideActionBar()

        Handler(Looper.myLooper()!!).postDelayed({
            setupViewModel()
        }, splashDuration)

    }

    private fun setupViewModel() {
        splashViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[SplashViewModel::class.java]

        splashViewModel.getSession().observe(this) { session ->
            if (session.userId.isEmpty() && session.name.isEmpty() && session.token.isEmpty()) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
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

    companion object {
        const val splashDuration = 1500L
    }
}