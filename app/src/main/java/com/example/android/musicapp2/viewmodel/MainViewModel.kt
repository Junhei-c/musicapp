package com.example.android.musicapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.model.MediaTypeEnum
import com.example.android.musicapp2.repository.DataRepository

class MainViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _allData = repository.getMediaList()
    private val _data = MutableLiveData<List<DataModel>>(_allData)
    val data: LiveData<List<DataModel>> get() = _data

    private val _selectedMode = MutableLiveData(-1)
    val selectedMode: LiveData<Int> get() = _selectedMode

    fun setSelectedMode(mode: Int) {
        _selectedMode.value = mode
    }

    fun filterDataByType(type: MediaTypeEnum) {
        _data.value = _allData.filter { it.mediaType == type }
    }
}

