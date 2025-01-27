package fr.enssat.singwithme.Leroux

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.Executors

class MusicApi {
    fun parseJSON(url: String): List<Music> {
        val jsons: List<String> =  URL(url).readText().lines()
        val listMusics: MutableList<Music> = mutableListOf()
        for (json in jsons){
            val music: Music = Gson().fromJson(json, Music::class.java)
            listMusics.add(music)
        }
        return listMusics.toList()
        /*val moshi: Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, Music::class.java)
        val jsonAdapter: JsonAdapter<List<Music>> = moshi.adapter(listType)
        val listMusics: List<Music> = jsonAdapter.fromJson(json)!!
        return listMusics*/
    }

    fun readList(): List<Music>{
        val jsons: List<String> = listOf(
            """
  {
    "name": "Hey Jude",
    "artist": "The Beatles",
    "locked": true
  }""",
  """{
    "name": "Wake Me up When September Ends",
    "artist": "Green Day",
    "locked": true
  }""",
  """{
    "name": "21 Guns",
    "artist": "Green Day",
    "locked": true
  }""",
  """{
    "name": "Wonderwall - Remastered",
    "artist": "Oasis",
    "locked": true
  }""",
  """{
    "name": "Don't Look Back in Anger - Remastered",
    "artist": "Oasis",
    "path": "DontLookBack/DontLookBack.md"
  }""",
  """{
    "name": "Married With Children",
    "artist": "Oasis",
    "locked": true
  }""",
  """{
    "name": "Stand by Me",
    "artist": "Oasis",
    "locked": true
  }""",
  """{
    "name": "Bohemian Rhapsody",
    "artist": "Queen",
    "path": "Bohemian/Bohemian.md"
  }""",
  """{
    "name": "Love Me Like There's No Tomorrow - Special Edition",
    "artist": "Freddie Mercury",
    "locked": true
  }""",
  """{
    "name": "Love Of My Life",
    "artist": "Queen",
    "locked": true
  }""",
  """{
    "name": "Basket Case",
    "artist": "Green Day",
    "locked": true
  }""",
  """{
    "name": "Disenchanted",
    "artist": "My Chemical Romance",
    "locked": true
  }""",
  """{
    "name": "Cancer",
    "artist": "My Chemical Romance",
    "locked": true
  }""",
  """{
    "name": "Alexandra",
    "artist": "Reality Club",
    "locked": true
  }""",
  """{
    "name": "Wish You Were Here",
    "artist": "Neck Deep",
    "locked": true
  }""",
  """{
    "name": "Last Night on Earth",
    "artist": "Green Day",
    "locked": true
  }""",
  """{
    "name": "December",
    "artist": "Neck Deep",
    "locked": true
  }""",
  """{
    "name": "Twist And Shout - Remastered 2009",
    "artist": "The Beatles",
    "locked": true
  }""",
  """{
    "name": "There Is a Light That Never Goes Out - 2011 Remaster",
    "artist": "The Smiths",
    "locked": true
  }""",
  """{
    "name": "Please, Please, Please, Let Me Get What I Want - 2011 Remaster",
    "artist": "The Smiths",
    "locked": true
  }""",
  """{
    "name": "Heaven Knows I'm Miserable Now - 2011 Remaster",
    "artist": "The Smiths",
    "locked": true
  }""",
  """{
    "name": "No Surprises",
    "artist": "Radiohead",
    "locked": true
  }""",
  """{
    "name": "Creep",
    "artist": "Radiohead",
    "path": "Creep/creep.md"
  }""",
  """{
    "name": "Dear God",
    "artist": "Avenged Sevenfold",
    "locked": true
  }""",
  """{
    "name": "Stop Crying Your Heart Out",
    "artist": "Oasis",
    "locked": true
  }
"""
        )
        val listMusics: MutableList<Music> = mutableListOf()
        for (json in jsons){
            val music: Music = Gson().fromJson(json, Music::class.java)
            listMusics.add(music)
        }
        return listMusics.toList()
    }

    private fun timeStringToSeconds(time: String): Int {
        val parts = time.split(":").map { it.toInt() }
        return parts[0] * 60 + parts[1]
    }

    private fun downloadMarkdownFile(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: throw IllegalStateException("Empty response body")
    }

    fun parseMD(file: String): Song{
        val lines = file.lines()
        var title = ""
        var author = ""
        var soundtrack = ""
        val lyric = mutableListOf<LyricsLine>()

        var inLyricsSection = false

        for (line in lines) {
            when {
                line.startsWith("# title") -> title = line.removePrefix("# title").trim()
                line.startsWith("# author") -> author = line.removePrefix("# author").trim()
                line.startsWith("# soundtrack") -> soundtrack = line.removePrefix("# soundtrack").trim()
                line.startsWith("# lyrics") -> inLyricsSection = true
                inLyricsSection -> {
                    val match =
                        Regex("\\{ (\\d+:\\d{2}) }(?:.*?\\{ (\\d+:\\d{2}) })?(.*)").find(line)
                    if (match != null) {
                        val (start, end, text) = match.destructured
                        val startTimestamp = timeStringToSeconds(start)
                        val endTimestamp = if (end.isNotBlank()) timeStringToSeconds(end) else null
                        lyric.add(LyricsLine(timeStart = startTimestamp, timeStop = endTimestamp, lyric = text.trim()))
                    }
                }
            }
        }

        return Song(
            title = title,
            author = author,
            soundtrack = soundtrack,
            lyrics = lyric
        )
    }

    fun parseMarkdownFromUrlAsync(url: String, onResult:(Song) -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val markdownContent = downloadMarkdownFile(url)
                val song = parseMD(markdownContent) // Votre fonction de parsing
                withContext(Dispatchers.Main) {
                    onResult(song)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

}