package com.example.musicplayer.service


import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import com.example.musicplayer.constant.Constant
import com.example.musicplayer.handler.MusicHandler
import com.example.musicplayer.helper.NotificationHelper
import com.example.musicplayer.model.MediaFileModel


class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private val binder = LocalBinder()
    private lateinit var modelMedia: MediaFileModel
    private var mediaPlayer: MediaPlayer? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == Constant.MUSIC_SERVICE_STOP) {
            stopForeground(true)
            stopSelf()
        }else if (intent?.action == Constant.MUSIC_SERVICE_PAUSE){
            pause()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    fun playForegroundMusic(mMediaFileModel: MediaFileModel) {
        NotificationHelper.startForeground(this, mMediaFileModel)

        modelMedia = mMediaFileModel
        mediaPlayer?.stop()  //Songs don't play simultaneously in MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(modelMedia.path)
            setOnPreparedListener(this@MusicService)
            setOnErrorListener(this@MusicService)
            setWakeMode(this@MusicService, PowerManager.PARTIAL_WAKE_LOCK)
            mediaPlayer?.isLooping = true
            prepareAsync()
        }
        MusicHandler.handler?.postDelayed(updateSongTime, 100)
    }


    fun playMusic(mMediaFileModel: MediaFileModel) {
        stopForeground(true)

        modelMedia = mMediaFileModel
        mediaPlayer?.stop()  //Songs don't play simultaneously in MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(modelMedia.path)
            setOnPreparedListener(this@MusicService)
            setOnErrorListener(this@MusicService)
            mediaPlayer?.isLooping = true
            prepareAsync()
        }
        MusicHandler.handler?.postDelayed(updateSongTime, 100)
    }



    private var updateSongTime = object : Runnable {
        override fun run() {
            if (MusicHandler.handler == null) return
            val message = MusicHandler.handler!!.obtainMessage()
            message.what = mediaPlayer?.currentPosition ?: 0
            MusicHandler.handler?.sendMessage(message)


            mediaPlayer?.setOnCompletionListener {
                val messagee = MusicHandler.handler!!.obtainMessage()
                messagee.what = -10
                MusicHandler.handler!!.sendMessage(messagee)
            }
            MusicHandler.handler!!.postDelayed(this, 1000)
        }
    }


    fun rewindMusic() {
        val rewind = mediaPlayer!!.currentPosition - 2500
        mediaPlayer?.seekTo(rewind)
    }

    fun forwardMusic() {
        val forward = mediaPlayer!!.currentPosition + 2500
        mediaPlayer?.seekTo(forward)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun progressChanged(seconds: Int) {
        val progress = seconds * 1000
        mediaPlayer?.seekTo(progress)
    }


    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        MusicHandler.handler = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of MusicService so clients can call public methods
        fun getService(): MusicService = this@MusicService
    }


}