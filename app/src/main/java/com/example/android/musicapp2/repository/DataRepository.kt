package com.example.android.musicapp2.repository

import com.example.android.musicapp2.model.DataModel

class DataRepository {
    fun getData(): List<DataModel> {
        return listOf(
            DataModel(
                id = 1,
                name = "Lo-Fi Chime",
                url = "https://cdn.freesound.org/previews/795/795963_2061858-lq.mp3",
                imageUrl = "https://i.imgur.com/LOFI1.png"
            ),
            DataModel(
                id = 2,
                name = "Synth Key",
                url = "https://cdn.freesound.org/previews/795/795893_462105-lq.mp3",
                imageUrl = "https://i.imgur.com/SYNTH2.png"
            ),
            DataModel(
                id = 3,
                name = "Dream Tone",
                url = "https://cdn.freesound.org/previews/797/797794_5674468-lq.mp3",
                imageUrl = "https://i.imgur.com/DREAM3.png"
            )
        )
    }
}
