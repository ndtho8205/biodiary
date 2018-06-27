package edu.bk.thesis.biodiary.core.face

import android.util.Log
import edu.bk.thesis.biodiary.utils.StorageHelper
import org.bytedeco.javacpp.opencv_core.Mat
import org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2BGR
import org.bytedeco.javacpp.opencv_imgproc.cvtColor

data class Face(val containerImage: Mat,
                var faceImageName: String,
                val faceImage: Mat,
                val boundingBox: BoundingBox)
{

    val alignedFaceImage: Mat

    init
    {
        alignedFaceImage = preprocessing(faceImage)
    }

    fun save()
    {
        val faceImagePath = StorageHelper.retrieveImagePath("${faceImageName}_face")
        val containerImagePath = StorageHelper.retrieveImagePath("${faceImageName}_container")

        Log.d("Face", "Save face to $faceImagePath")

        JavaCvUtils.imsave(faceImagePath, alignedFaceImage)

        val bgrImage = Mat()
        cvtColor(containerImage, bgrImage, COLOR_RGB2BGR)
        JavaCvUtils.imsave(containerImagePath, bgrImage)
    }

    private fun preprocessing(faceImage: Mat): Mat
    {
        val preprocessor = ImagePreprocessing()
        val aligned = preprocessor.equalizeHist(preprocessor.scaleToStandardSize(faceImage))
        return aligned
    }

    data class BoundingBox(val x: Int, val y: Int, val w: Int, val h: Int)
}
