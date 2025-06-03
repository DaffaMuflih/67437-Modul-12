package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)

        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            applicationContext
        )

        return withContext(Dispatchers.IO) {
            try {
                if (resourceUri.isNullOrBlank()) {
                    val errorMessage =
                        applicationContext.resources.getString(R.string.invalid_input_uri)
                    Log.e(TAG, errorMessage)
                    return@withContext Result.failure()
                }

                val resolver = applicationContext.contentResolver

                delay(DELAY_TIME_MILLIS)

                // Buka stream gambar dari Uri dan decode menjadi Bitmap
                val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri))
                ) ?: return@withContext Result.failure()

                // Proses blur (fungsi Anda harus ada)
                val output = blurBitmap(picture, blurLevel)

                // Simpan hasil bitmap ke file dan dapatkan Uri output
                val outputUri = writeBitmapToFile(applicationContext, output)

                val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

                makeStatusNotification(
                    "Output is $outputUri",
                    applicationContext
                )

                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )
                Result.failure()
            }
        }
    }

    // Fungsi notifikasi (anda harus punya implementasi)
    private fun makeStatusNotification(message: String, context: Context) {
        // Implementasikan notifikasi status pekerjaan di sini jika perlu
    }

    // Fungsi blurBitmap dan writeBitmapToFile harus diimplementasikan di tempat lain
}
