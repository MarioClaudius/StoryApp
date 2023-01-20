package android.marc.com.storyapp.activity.storydetail

import android.content.Context
import android.marc.com.storyapp.helper.Helper.withDateFormat
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.databinding.ActivityStoryDetailBinding
import android.marc.com.storyapp.model.SessionPreference
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var storyDetailViewModel: StoryDetailViewModel
    private lateinit var storyId: String
    private lateinit var auth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyId = intent.getStringExtra(MainActivity.STORY_ID_KEY_EXTRA) ?: ""

        hideActionBar()
        getToken()
        setupViewModel()
    }

    private fun setupViewModel(){
        storyDetailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[StoryDetailViewModel::class.java]

        if (storyId.isNotEmpty()) {
            storyDetailViewModel.getStoryDetail(storyId, auth)
        }

        storyDetailViewModel.storyDetail.observe(this) { storyDetail ->
            binding.apply {
                Glide.with(this@StoryDetailActivity)
                    .load(storyDetail.photoUrl)
                    .into(imgStoryDetail)
                tvCreatorName.text = getString(R.string.story_creator, storyDetail.name)
                tvStoryDate.text = storyDetail.createdAt.withDateFormat()
                tvStoryDescription.text = storyDetail.description
            }
        }

        storyDetailViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getToken() {
        var token: String?
        runBlocking { token = SessionPreference.getInstance(dataStore).getSessionToken().first() }
        this.auth = "Bearer $token"
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