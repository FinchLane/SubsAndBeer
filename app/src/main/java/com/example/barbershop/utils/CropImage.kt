package com.example.barbershop.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.BuildConfig
import com.yalantis.ucrop.UCrop
import timber.log.Timber

class CropImage(
    private val aspectRatioX: Float = 1f,
    private val aspectRatioY: Float = 1f
) : ActivityResultContract<Pair<Uri, Uri>, Uri?>()
{
    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent = UCrop.of(input.first, input.second)
        .withAspectRatio(aspectRatioX, aspectRatioY)
        .getIntent(context)

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return null
        } else if (BuildConfig.DEBUG && resultCode == UCrop.RESULT_ERROR) {
            UCrop.getError(intent)?.let { cropError ->
                Timber.e("cropError: $cropError")
            }
        }
        return UCrop.getOutput(intent)
    }
}