package edu.bk.thesis.biodiary.core.face

import android.util.Log
import edu.bk.thesis.biodiary.utils.StorageHelper
import org.bytedeco.javacpp.opencv_core.Mat
import org.bytedeco.javacpp.opencv_imgproc

data class Face(val containerImage: Mat, val image: Mat, val boundingBox: BoundingBox,
                val containerImageName: String)
{

    var alignedImage: Mat = image

    fun save()
    {
        val faceImagePath = StorageHelper.retrieveImagePath("${containerImageName}_face")
        val containerImagePath = StorageHelper.retrieveImagePath("${containerImageName}_container")

        Log.d("Face", "Save face to $faceImagePath")
        if (faceImagePath != null && containerImagePath != null)
        {
            JavaCvUtils.imsave(faceImagePath, alignedImage)

            val bgrImage = Mat()
            opencv_imgproc.cvtColor(containerImage, bgrImage, opencv_imgproc.COLOR_RGB2BGR)
            JavaCvUtils.imsave(containerImagePath, bgrImage)
        } else
        {
            Log.e("Face", "imagePath == null")
        }
    }

    data class BoundingBox(val x: Int, val y: Int, val w: Int, val h: Int)
}
