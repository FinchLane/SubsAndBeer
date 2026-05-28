package com.example.barbershop.viewmodel.galery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryViewModel(private val context: Context) : ViewModel() {
    private val _mediaFiles = MutableLiveData<List<Uri>>()
    val mediaFiles: LiveData<List<Uri>> = _mediaFiles

    fun loadMediaFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = getMediaFiles(context)
            _mediaFiles.postValue(files)
        }
    }
}