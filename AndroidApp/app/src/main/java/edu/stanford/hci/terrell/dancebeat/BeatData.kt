package edu.stanford.hci.terrell.dancebeat

data class Beat (
    val t: Float,
    val b: Float,
        )
data class BeatData (
    val beats: List<Beat>
)