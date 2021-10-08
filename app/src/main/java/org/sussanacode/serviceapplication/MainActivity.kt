package org.sussanacode.serviceapplication

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
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


        val serviceIntent = Intent(baseContext, MusicService::class.java)

        if(isServiceRunnig(MusicService::class.java)){
            isPlayerStopped = false
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        }


        setUpEvents()

    }

    private fun setUpEvents() {

        binding.startMusic.setOnClickListener {
            val serviceIntent = Intent(baseContext, MusicService::class.java)

            if(isServiceRunnig(MusicService::class.java)){
                bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
            }else{

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(serviceIntent)
                else
                    startService(serviceIntent)
            }

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

            if(isPlayerStopped){
                musicBinder.start()
            }
            //musicBinder.start()
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


    fun isServiceRunnig(serviceClass: Class<*> ): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any{it.service.className == serviceClass.name}
    }


}