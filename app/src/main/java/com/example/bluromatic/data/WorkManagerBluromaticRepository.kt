package com.example.bluromatic.data

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.workers.BlurWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import com.example.bluromatic.getImageUri

class WorkManagerBluromaticRepository(context: Context) : BluromaticRepository {
    private val workManager = WorkManager.getInstance(context)
    private var imageUri: Uri = context.getImageUri()

    private val _outputWorkInfo = MutableStateFlow<WorkInfo?>(null)
    override val outputWorkInfo: Flow<WorkInfo?> = _outputWorkInfo

    private var currentWorkId: UUID? = null

    override fun applyBlur(blurLevel: Int, imageUri: Uri) {
        val inputData = createInputDataForWorkRequest(blurLevel, imageUri)
        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(inputData)
            .build()

        currentWorkId = blurRequest.id

        workManager.enqueue(blurRequest)

        workManager.getWorkInfoByIdLiveData(blurRequest.id)
            .observeForever { workInfo ->
                _outputWorkInfo.value = workInfo
            }
    }

    override fun cancelWork() {
        currentWorkId?.let { id ->
            workManager.cancelWorkById(id)
            currentWorkId = null
        }
    }

    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: Uri): Data {
        return Data.Builder()
            .putString(KEY_IMAGE_URI, imageUri.toString())
            .putInt(KEY_BLUR_LEVEL, blurLevel)
            .build()
    }
}
