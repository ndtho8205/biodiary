package edu.bk.thesis.biodiary.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

object StorageHelper
{

    const val TAG = "StorageHelper"

    private fun isExternalStorageWritable(): Boolean
    {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun setupStorageDir()
    {
        if (!isExternalStorageWritable())
            throw IOException("Cannot open external storage.")

        val file = File(Environment.getExternalStorageDirectory(), "BioDiary")
        val face = File(file, "Face")
        val voice = File(file, "Voice")
        file.mkdirs()
        face.mkdirs()
        voice.mkdirs()
    }

    private fun getStorageDir(): File?
    {
        if (!isExternalStorageWritable())
            return null

        val file = File(Environment.getExternalStorageDirectory(), "BioDiary")
        if (!file.mkdirs())
        {
            Log.e(TAG, "Directory not created")
        }

        return file
    }

    fun retrieveImagePath(imageName: String): String?
    {
        val dir = getStorageDir() ?: return null
        return File(dir, "Face/$imageName.png").absolutePath
    }

    fun retrieveAudioPath(audioName: String): String?
    {
        val dir = getStorageDir() ?: return null
        return File(dir, "Voice/$audioName.wav").absolutePath
    }

    fun retrievePrivatePath(context: Context, filename: String): String
    {
        return context.getFileStreamPath(filename).absolutePath
    }
}
