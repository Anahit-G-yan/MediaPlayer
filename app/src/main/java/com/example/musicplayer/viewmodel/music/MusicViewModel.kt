package com.example.musicplayer.viewmodel.music

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.model.MediaFileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    /**
     *  Properties of media files
     */
    private lateinit var modelMedia: MediaFileModel
    private lateinit var songList: ArrayList<MediaFileModel>
    private var position by Delegates.notNull<Int>()


    /**
     *  Live data of music functionality
     */
    val playNextLiveData: MutableLiveData<MediaFileModel> = MutableLiveData()
    val playPrevLiveData: MutableLiveData<MediaFileModel> = MutableLiveData()
    val buttonsPressedLiveData: MutableLiveData<Unit> = MutableLiveData()

    fun checkingButtonsPressed() {

        CoroutineScope(Dispatchers.IO).launch {
            startTime()
        }
    }

    private suspend fun startTime() {
        delay(1000)
        buttonsPressedLiveData.postValue(Unit)
        startTime()
    }


    fun currentMedia(): MediaFileModel = modelMedia

    fun addMediaData(modelMedia: MediaFileModel, songList: ArrayList<MediaFileModel>, position: Int) {
        this.modelMedia = modelMedia
        this.songList = songList
        this.position = position
    }

    fun playPrev() {
        if (arrangementForPrevMusic(position, songList)) {
            modelMedia = songList[position - 1]
            playPrevLiveData.value = modelMedia
            position--
        } else {
            position = songList.size - 1
            modelMedia = songList[position]
            playPrevLiveData.value = modelMedia
        }
    }

    fun playNext() {
        if (arrangementForNextMusic(position, songList)) {
            modelMedia = songList[position + 1]
            playNextLiveData.value = modelMedia
            position++
        } else {
            position = 0
            modelMedia = songList[position]
            playNextLiveData.value = modelMedia
        }
    }


    private fun arrangementForNextMusic(position: Int, songList: ArrayList<MediaFileModel>): Boolean {
        return position != songList.size - 1
    }

    private fun arrangementForPrevMusic(position: Int, songList: ArrayList<MediaFileModel>): Boolean {
        return position != 0
    }

}