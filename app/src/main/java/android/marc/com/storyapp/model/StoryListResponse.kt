package android.marc.com.storyapp.model

import com.google.gson.annotations.SerializedName

data class StoryListResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<Story>
)