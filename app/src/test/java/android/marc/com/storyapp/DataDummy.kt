package android.marc.com.storyapp

import android.marc.com.storyapp.model.Story

object DataDummy {
    fun generateDummyStoryData(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                i.toString(),
                "story$i",
                "description$i",
            "photoUrl$i",
                "createdAt$i",
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}