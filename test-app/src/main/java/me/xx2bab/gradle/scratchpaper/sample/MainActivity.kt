package me.xx2bab.gradle.scratchpaper.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paulvarry.jsonviewer.JsonViewer
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<JsonViewer>(R.id.json_view).apply {
            setJson(JSONObject(loadScratchPaper()))
            expandJson()
        }
    }

    private fun loadScratchPaper(): String {
        return assets.open("scratch-paper.json").bufferedReader().use { it.readText() }
    }

}

