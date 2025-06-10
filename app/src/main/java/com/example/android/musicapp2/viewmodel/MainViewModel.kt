package com.example.android.musicapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.repository.DataRepository

class MainViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _data = MutableLiveData<List<DataModel>>()
    val data: LiveData<List<DataModel>> = _data

    private val _selectedMode = MutableLiveData(-1)
    val selectedMode: LiveData<Int> = _selectedMode

    init {
        _data.value = repository.getMediaList()
    }

    fun setSelectedMode(mode: Int) {
        _selectedMode.value = mode
    }
}

