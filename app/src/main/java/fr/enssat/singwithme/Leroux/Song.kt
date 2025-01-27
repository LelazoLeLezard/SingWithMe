package fr.enssat.singwithme.Leroux

data class Song(
    val title: String,
    val author: String,
    val soundtrack: String,
    val lyrics: List<LyricsLine>
)
