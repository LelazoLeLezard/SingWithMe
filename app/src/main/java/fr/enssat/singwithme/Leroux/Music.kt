package fr.enssat.singwithme.Leroux

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Music(
    val name: String,
    val artist: String,
    val locked: Boolean = false,
    val path: String = ""
)


