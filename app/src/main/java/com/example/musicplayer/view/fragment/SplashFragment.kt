package com.example.musicplayer.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.R
import com.example.musicplayer.view.activity.MediaActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//TODO: please add final comments in functions
//TODO: please create base fragment
class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // for changing status and navigation bar colors
        handleStatusAndNavigationBarColor()
        // checking corresponding permissions
        lifecycleScope.launch {
            handleCheckPermission()
        }
    }

    private suspend fun handleCheckPermission() {
        delay(1000)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // This case permission is ok
            val mediaActivity = activity as MediaActivity
            mediaActivity.openFragment(R.id.fragmentContainer, MediaFragments())
        } else {
            // request corresponding permission
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    private fun handleStatusAndNavigationBarColor() {
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.transparent)
        window.navigationBarColor = this.resources.getColor(R.color.transparent)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                val mediaActivity = activity as MediaActivity
                mediaActivity.openFragment(R.id.fragmentContainer, MediaFragments())
            } else {
                requireActivity().finish()
            }
        }

}