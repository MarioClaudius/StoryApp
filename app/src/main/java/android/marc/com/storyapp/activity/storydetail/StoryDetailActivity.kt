package android.marc.com.storyapp.activity.storydetail

import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.databinding.ActivityStoryDetailBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(MainActivity.STORY_ID_KEY_EXTRA)
        Log.d("CEK ID", storyId!!)
    }
}