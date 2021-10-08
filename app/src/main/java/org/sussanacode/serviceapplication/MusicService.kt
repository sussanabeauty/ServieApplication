package org.sussanacode.serviceapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

class MusicService : Service() {
    var musicControlCmd = ""
    lateinit var mediaPlayer: MediaPlayer
    lateinit var musicBinder : MusicBinder

    var isMediaPlayerReady = false

    override fun onCreate() {
        super.onCreate()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startAForegroundService()
        }

//        mediaPlayer = MediaPlayer()
    }

    private fun startAForegroundService() {
        val id = Random.nextInt(50000)


        val playIntent = Intent(baseContext, MusicService::class.java)
        playIntent.putExtra("cmd", "play")
        val pendingIntentPlay = PendingIntent.getService(this, id, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(baseContext, MusicService::class.java)
        pauseIntent.putExtra("cmd", "pause")
        val pendingIntentPause = PendingIntent.getService(this, id, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val stopIntent = Intent(baseContext, MusicService::class.java)
        stopIntent.putExtra("cmd", "stop")
        val pendingIntentStop = PendingIntent.getService(this, id, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val contentIntent = Intent(baseContext, MainActivity::class.java)
        val piActivity = PendingIntent.getActivity(baseContext, id, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notification = NotificationCompat.Builder(this, "MusicPlayer")
            .setContentTitle("Now Playing - Hallelujah-Tori_Kelly")
            .setContentText("Media Player in background")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(R.drawable.ic_music_note, "play", pendingIntentPlay)
            .addAction(R.drawable.ic_music_note, "pause", pendingIntentPause)
            .addAction(R.drawable.ic_music_note, "stop", pendingIntentStop)
            .setContentIntent(piActivity)

            .setOngoing(true).setAutoCancel(false)
            .build()


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("MusicPlayer", "Music Player", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

            startForeground(id, notification)



    }



    override fun onBind(intent: Intent): IBinder? {
        if(this::musicBinder.isInitialized){
            return musicBinder
        }

       // startMusic()
        musicBinder = MusicBinder()
        return musicBinder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        musicControlCmd = intent?.extras?.getString("cmd")?:""

        when (musicControlCmd){

            "start" -> {startMusic()}

            "pause" -> {pauseMusic()}

            "play" -> {playMusic()}

            "stop" -> {stopMusic()}
        }
        return START_STICKY

        //return super.onStartCommand(intent, flags, startId)
    }

    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun playMusic() {
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    private fun pauseMusic() {

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }

    }


    private fun startMusic() {

        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource("/Music/iTunes/Album Artwork/Download/Hallelujah-Tori_Kelly")

        mediaPlayer.setOnPreparedListener {
            it.start()
            isMediaPlayerReady = true
        }

        mediaPlayer.prepareAsync()


    }


//    private fun startMusic() {
//
//        //"C:\Users\Sussana Beauty Kwabi\Music\iTunes\Album Artwork\Download\Ntokozo_Mbambo_Joe_Mettle_-_Amen"
//       // "C:/Users/Sussana Beauty Kwabi/Music/iTunes/Album Artwork/Download/Ntokozo_Mbambo_Joe_Mettle_-_Amen"
//
//        mediaPlayer = MediaPlayer()
//
//        val musicPath = "https://nl03.moozix.com/31427eed5cdcd6ad3c45e/Hallelujah.mp3"
//
//        val file = File(musicPath)
//        val inputStream = FileInputStream(file)
//
//        //mediaPlayer.setDataSource("C:\\Users\\Sussana Beauty Kwabi\\Music\\iTunes\\Album Artwork\\Download\\Ntokozo_Mbambo_Joe_Mettle_-_Amen")
//        mediaPlayer.setDataSource(inputStream.fd)
//
//        mediaPlayer.setOnPreparedListener {
//            it.start()
//            isMediaPlayerReady = true
//        }
//
//        mediaPlayer.prepareAsync()
//        inputStream.close()
//
//    }

    override fun onDestroy() {
        super.onDestroy()
    }


    inner class MusicBinder: Binder() {

        fun start(){startMusic()}

        fun pause(){pauseMusic()}

        fun play(){playMusic()}

        fun stop(){stopMusic()}



        fun getTotalTime(): Int {

            if(isMediaPlayerReady){
                return mediaPlayer.duration
            }

            return  -1

        }

        fun getCurrentSeekPosition(): Int {

            if(isMediaPlayerReady){
                return mediaPlayer.currentPosition
            }
            return -1
        }

        fun setCurrentSeekPosition(newPosition: Int){
            mediaPlayer.seekTo(newPosition)
        }
    }
}