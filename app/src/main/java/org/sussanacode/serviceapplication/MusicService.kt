package org.sussanacode.serviceapplication

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import java.io.File
import java.io.FileInputStream

class MusicService : Service() {
    var musicControlCmd = ""
    lateinit var mediaPlayer: MediaPlayer
    lateinit var musicBinder : MusicBinder

    var isMediaPlayerReady = false

    override fun onCreate() {
        super.onCreate()

//        mediaPlayer = MediaPlayer()
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

        return super.onStartCommand(intent, flags, startId)
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