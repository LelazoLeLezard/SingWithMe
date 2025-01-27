package fr.enssat.singwithme.Leroux

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.enssat.singwithme.Leroux.ui.theme.SIngWithMeTheme

class KaraokeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val path = intent.getStringExtra("path") ?: "Unknow"
        val name = intent.getStringExtra("name") ?: "Unknow"
        setContent {
            SIngWithMeTheme {
                    KaraokeElement(path = path, name = name)
                }
            }
        }
    }

@Composable
fun KaraokeElement(name: String,path: String, modifier: Modifier = Modifier){
    val musicApi = MusicApi()
    val mediaPlayer = remember { MediaPlayer() }
    var song by remember { mutableStateOf<Song?>(null) }

    musicApi.parseMarkdownFromUrlAsync(
        url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$path",
        onResult = { songs ->
            song = songs
        },
        onError = { exception ->
            println("Error: ${exception.message}")
        })

    LaunchedEffect(Unit) {
        while (true){
            kotlinx.coroutines.delay(1000)
        }
    }

    LaunchedEffect(song?.soundtrack) {
        if (song?.soundtrack != null) {
            try {
                val soundtrackUrl = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/${song!!.soundtrack}"
                mediaPlayer.reset()
                mediaPlayer.setDataSource(soundtrackUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    it.start()
                }
            } catch (e: Exception) {
                println("Error in MediaPlayer: ${e.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer.release() }
    }

    if(song != null) {
        Text(
            text = mediaPlayer.currentPosition.toString(),
            modifier = modifier.padding(20.dp)
        )
    } else {
        Text(text = "Chargement...", modifier = modifier.padding(20.dp))
    }

}

