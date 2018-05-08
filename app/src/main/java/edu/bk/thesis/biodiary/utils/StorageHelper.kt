package edu.bk.thesis.biodiary.utils

import android.os.Environment
import android.util.Log
import java.io.File

object StorageHelper
{

    const val TAG = "StorageHelper"

    private fun isExternalStorageWritable(): Boolean
    {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun getPublicAlbumStorageDir(): File?
    {
        if (!isExternalStorageWritable())
            return null

        val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "BioDiary")
        if (!file.mkdirs())
        {
            Log.e(TAG, "Directory not created")
        }

        return file
    }

    fun retrieveImagePath(imageName: String): String?
    {
        val albumPath = getPublicAlbumStorageDir() ?: return null
        return File(albumPath, "$imageName.png").absolutePath
    }
}
