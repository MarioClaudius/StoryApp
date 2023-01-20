package android.marc.com.storyapp.activity.main

import android.content.Context
import android.content.Intent
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.activity.addstory.AddStoryActivity
import android.marc.com.storyapp.activity.login.LoginActivity
import android.marc.com.storyapp.activity.storydetail.StoryDetailActivity
import android.marc.com.storyapp.adapter.StoryListAdapter
import android.marc.com.storyapp.api.ApiConfig
import android.marc.com.storyapp.databinding.ActivityMainBinding
import android.marc.com.storyapp.model.SessionPreference
import android.marc.com.storyapp.model.Story
import android.marc.com.storyapp.model.StoryListResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var rvStory: RecyclerView
    private lateinit var auth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvStory = binding.rvStoryList
        rvStory.setHasFixedSize(true)

        getToken()
        setupViewModel()
    }

    private fun setupRecyclerViewAdapter(storyList: List<Story>) {
        rvStory.layoutManager = GridLayoutManager(this, 2)
        val storyListAdapter = StoryListAdapter(storyList)
        rvStory.adapter = storyListAdapter

        storyListAdapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback{
            override fun onItemClicked(storyId: String) {
                val intentToDetail = Intent(this@MainActivity, StoryDetailActivity::class.java)
                intentToDetail.putExtra(STORY_ID_KEY_EXTRA, storyId)
                startActivity(intentToDetail)
            }
        })
    }

    private fun getToken() {
        var token: String?
        runBlocking { token = SessionPreference.getInstance(dataStore).getSessionToken().first() }
        this.auth = "Bearer ${token}"
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.displayStoryList(null, null, null, auth)

        mainViewModel.storyList.observe(this) { storyList ->
            setupRecyclerViewAdapter(storyList)
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        mainViewModel.getSession().observe(this) { session ->
            if (session.userId.isEmpty() && session.name.isEmpty() && session.token.isEmpty()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_story -> {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
                true
            }
            R.id.change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                mainViewModel.logout()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        const val STORY_ID_KEY_EXTRA = "story_id_key_extra"
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.displayStoryList(null, null, null, auth)
    }
}