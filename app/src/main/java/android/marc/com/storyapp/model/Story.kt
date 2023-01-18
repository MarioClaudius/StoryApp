package android.marc.com.storyapp.model

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val latitude: Double,
    val longitude: Double
)
