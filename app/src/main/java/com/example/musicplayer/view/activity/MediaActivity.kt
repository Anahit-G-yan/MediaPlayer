package com.example.musicplayer.view.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.controller.SharedController
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.view.fragment.MediaFragments
import com.example.musicplayer.view.fragment.SplashFragment

class MediaActivity : AppCompatActivity() {

    lateinit var mMusicService: MusicService
    private var mBound: Boolean = false


    ////    /** Defines callbacks for service binding, passed to bindService()  */
    val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to MusicService, cast the IBinder and get MusicService instance
            val binder = service as MusicService.LocalBinder
            mMusicService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runService()
        //TODO: need to create base Activity and move openFragment function into there
        openFragment(R.id.fragmentContainer, SplashFragment())
    }



    private fun runService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
            startService(serviceIntent)
        } else {
            // Bind to LocalService
            serviceIntent.also { intent ->
                startService(intent)
            }
        }
    }


    fun openFragment(container: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(container, fragment, fragment::class.java.name)
            .addToBackStack(fragment::class.java.name)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        // Bind to MusicService
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedController = SharedController(this)
        if (!sharedController.checkService()) {
            mMusicService.stopSelf()
        }
    }

}