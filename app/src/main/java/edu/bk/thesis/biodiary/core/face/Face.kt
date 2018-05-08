package edu.bk.thesis.biodiary.core.face

import android.util.Log
import edu.bk.thesis.biodiary.utils.StorageHelper
import org.bytedeco.javacpp.opencv_core.Mat

data class Face(val image: Mat, val boundingBox: BoundingBox, val containerImageName: String)
{

    var alignedImage: Mat = image

    fun save()
    {
        val imagePath = StorageHelper.retrieveImagePath(containerImageName)
        if (imagePath != null)
            JavaCvUtils.imsave(imagePath, alignedImage)
        else
        {
            Log.e("Face", "imagePath == null")
        }
    }

    data class BoundingBox(val x: Int, val y: Int, val w: Int, val h: Int)
}
