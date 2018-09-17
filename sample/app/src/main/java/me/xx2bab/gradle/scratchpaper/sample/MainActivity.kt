package me.xx2bab.gradle.scratchpaper.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.json).apply {
            text = loadScratchPaper()
            movementMethod = ScrollingMovementMethod()
        }
    }

    private fun loadScratchPaper(): String {
        return assets.open("scratch-paper.json").bufferedReader().use { it.readText() }
    }

}

