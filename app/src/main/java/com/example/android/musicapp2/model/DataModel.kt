package com.example.android.musicapp2.model

data class DataModel(
    val id: Int,
    val name: String,
    val url: String,
    val imageRes: Int,
    val mediaType: MediaTypeEnum = MediaTypeEnum.AUDIO
)


