package fr.enssat.singwithme.Leroux

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import fr.enssat.singwithme.Leroux.ui.theme.SIngWithMeTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIngWithMeTheme {
                val musicApi: MusicApi = MusicApi()
                //val listMusics: List<Music> = musicApi.parseJSON("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json")
                val listMusics: List<Music> = musicApi.readList()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListMusicElements(
                        listMusics = listMusics,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MusicElement(title: String, author: String, locked: Boolean, path: String = "unknow", modifier: Modifier = Modifier){
    val context = LocalContext.current
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp)
            .clickable {
                if (!locked) {
                    val intent = Intent(context, KaraokeActivity::class.java).apply {
                       putExtra("path", path);
                    }
                    context.startActivity(intent)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                modifier = modifier
            )
            Text(
                text = author,
                modifier = Modifier.padding(8.dp, 4.dp),
                fontSize = 10.sp,
                color = Color.DarkGray,
                )
        }
        if (locked){
            Image(
                Icons.Outlined.Close,
                contentDescription = "Cross Button",
                colorFilter = ColorFilter.tint(Color.Red)
            )
        }else {
            Image(
                Icons.Outlined.PlayArrow,
                contentDescription = "Button Play",
                colorFilter = ColorFilter.tint(Color.Green)
            )
        }
    }
}

@Composable
fun ListMusicElements(listMusics: List<Music>, modifier: Modifier = Modifier){
    LazyColumn (
        modifier = Modifier.padding(20.dp,50.dp)
    ){
        items(listMusics) { music ->
            MusicElement(music.name, music.artist, music.locked, music.path, modifier)
        }
    }
}


