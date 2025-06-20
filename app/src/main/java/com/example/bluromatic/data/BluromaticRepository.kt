package com.example.bluromatic.data

import android.net.Uri
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface BluromaticRepository {
    val outputWorkInfo: Flow<WorkInfo?>
    fun applyBlur(blurLevel: Int, imageUri: Uri)
    fun cancelWork()
}
