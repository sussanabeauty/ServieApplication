package org.sussanacode.serviceapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sussanacode.serviceapplication.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var musicBinder: MusicService.MusicBinder

    var isPlayerStopped = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpEvents()

    }

    private fun setUpEvents() {

        binding.startMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        }

        binding.btnPauseMusic.setOnClickListener {

            if(this::musicBinder.isInitialized){
                musicBinder.pause()
            }
        }


        binding.btnPlayMusic.setOnClickListener {
            if(this::musicBinder.isInitialized){
                musicBinder.play()
            }
        }

        binding.btnStopMusic.setOnClickListener {
            if(this::musicBinder.isInitialized){
                musicBinder.stop()
                isPlayerStopped = true
            }
        }


        binding.sbTrackProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, formUser: Boolean) {

                if(formUser && this@MainActivity::musicBinder.isInitialized){

                    musicBinder.setCurrentSeekPosition(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }


    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicBinder = binder as MusicService.MusicBinder

            musicBinder.start()
            isPlayerStopped = false

            setUpSeekBarPositionListener()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {}

    }


    private fun setUpSeekBarPositionListener() {

        lifecycleScope.launch(Dispatchers.Main){
            var totalTime = -1
            do{
                delay(1000)
                totalTime = musicBinder.getTotalTime()
//                delay(1000)

            }while(totalTime == -1)

            binding.sbTrackProgress.max = totalTime

            while(!isPlayerStopped){
                val currentPosition = musicBinder.getCurrentSeekPosition()
                if(currentPosition != -1) {
                    binding.sbTrackProgress.progress = currentPosition
                }
                delay(1000)

            }
        }
    }


}



//class MainActivity : AppCompatActivity() {
//    lateinit var binding: ActivityMainBinding
//    lateinit var musicBinder: MusicService.MusicBinder
//
//    var isPlayerStopped = true
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        setUpEvents()
//
//    }
//
//        private fun setUpEvents() {
//
//            binding.startMusic.setOnClickListener {
//                val serviceIntent = Intent(baseContext, MusicService::class.java)
//
//                serviceIntent.putExtra("cmd", "start")
//                //startService(serviceIntent)
//                bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
//                isPlayerStopped = false
//            }
//
//            binding.btnPauseMusic.setOnClickListener {
//                val serviceIntent = Intent(baseContext, MusicService::class.java)
//
//                serviceIntent.putExtra("cmd", "pause")
//                startService(serviceIntent)
//            }
//
//
//            binding.btnPlayMusic.setOnClickListener {
//                val serviceIntent = Intent(baseContext, MusicService::class.java)
//
//                serviceIntent.putExtra("cmd", "play")
//                startService(serviceIntent)
//            }
//
//            binding.btnStopMusic.setOnClickListener {
//                val serviceIntent = Intent(baseContext, MusicService::class.java)
//
//                serviceIntent.putExtra("cmd", "stop")
//                startService(serviceIntent)
//            }
//
//
//            binding.sbTrackProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(sb: SeekBar?, progress: Int, formUser: Boolean) {
//
//                    if(formUser && this@MainActivity::musicBinder.isInitialized){
//
//                        musicBinder.setCurrentSeekPosition(progress)
//                    }
//                }
//
//                override fun onStartTrackingTouch(p0: SeekBar?) {
//
//                }
//
//                override fun onStopTrackingTouch(p0: SeekBar?) {
//
//                }
//
//
//            })
//
//        }
//
//
//
//        val serviceConnection = object : ServiceConnection{
//            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
//               musicBinder = binder as MusicService.MusicBinder
//
//                setUpSeekBarPositionListener()
//
//
//            }
//
//            override fun onServiceDisconnected(p0: ComponentName?) {
//
//            }
//
//        }
//
//
//
//
//    private fun setUpSeekBarPositionListener() {
//
//        lifecycleScope.launch(Dispatchers.Main){
//            var totalTime = -1
//            do{
//                delay(1000)
//                totalTime = musicBinder.getTotalTime()
////                delay(1000)
//
//            }while(totalTime == -1)
//
//            binding.sbTrackProgress.max = totalTime
//
//            while(! isPlayerStopped){
//                val currentPosition = musicBinder.getCurrentSeekPosition()
//                if(currentPosition != -1) {
//                    binding.sbTrackProgress.progress = currentPosition
//                }
//                delay(1000)
//
//            }
//    }
//}
//
//
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unbindService(serviceConnection)
//    }
//}