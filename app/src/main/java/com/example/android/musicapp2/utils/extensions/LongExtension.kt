package com.example.android.musicapp2.utils.extensions

import android.util.Log

fun Int.toLongOrDefault(): Long {
    var result = 0L
    try {
        result = this.toLong()
    } catch (e: Exception) {
        Log.e("LongExtension", "Conversion error: ${e.localizedMessage}")
    }
    return result
}
