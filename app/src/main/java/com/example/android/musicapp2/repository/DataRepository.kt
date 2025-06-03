package com.example.android.musicapp2.repository

import com.example.android.musicapp2.R
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.model.MediaTypeEnum

class DataRepository {

    fun getMediaList(): List<DataModel> {
        return listOf(
            DataModel(
                id = 1,
                name = "Lo-FI Chime",
                url = "https://your-audio-url.com/lofi.mp3",
                imageRes = R.drawable.chime,
                mediaType = MediaTypeEnum.AUDIO
            ),
            DataModel(
                id = 2,
                name = "Synth Key",
                url = "https://your-audio-url.com/synth.mp3",
                imageRes = R.drawable.bigpiano,
                mediaType = MediaTypeEnum.AUDIO
            ),
            DataModel(
                id = 3,
                name = "Dream Tone",
                url = "https://your-audio-url.com/dream.mp3",
                imageRes = R.drawable.earlybirds,
                mediaType = MediaTypeEnum.AUDIO
            ),
            DataModel(
                id = 4,
                name = "Bunny Vid",
                url = "https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4",
                imageRes = R.drawable.group,
                mediaType = MediaTypeEnum.VIDEO
            ),
            DataModel(
                id = 5,
                name = "Elephant Vid",
                url = "https://archive.org/download/ElephantsDream/ed_hd.mp4",
                imageRes = R.drawable.group,
                mediaType = MediaTypeEnum.VIDEO
            ),
            DataModel(
                id = 6,
                name = "Demo Vid",
                url = "https://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov",
                imageRes = R.drawable.group,
                mediaType = MediaTypeEnum.VIDEO
            )
        )
    }
}




