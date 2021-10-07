package org.sussanacode.serviceapplication

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.sussanacode.serviceapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.startMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            serviceIntent.putExtra("cmd", "start")
            startService(serviceIntent)
        }

        binding.btnPauseMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            serviceIntent.putExtra("cmd", "pause")
            startService(serviceIntent)
        }


        binding.btnPauseMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            serviceIntent.putExtra("cmd", "play")
            startService(serviceIntent)
        }

        binding.btnPauseMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            serviceIntent.putExtra("cmd", "stop")
            startService(serviceIntent)
        }


        binding.sbTrackProgress.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            serviceIntent.putExtra("cmd", "stop")
            startService(serviceIntent)
        }
    }
}