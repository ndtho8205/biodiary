package edu.bk.thesis.biodiary.core.face

import android.content.Context
import android.util.Log
import edu.bk.thesis.biodiary.R
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2GRAY
import org.bytedeco.javacpp.opencv_imgproc.cvtColor
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier

class FaceDetection(context: Context)
{

    private val TAG = "FaceDetection"
    private val MIN_FACE_WIDTH_THRESHOLD = 160

    private var mFaceCascade: CascadeClassifier

    init
    {
        val inputStream = context.resources.openRawResource(R.raw.haarcascade_frontalface_alt)
        val content = inputStream.bufferedReader().use { it.readText() }

        context.openFileOutput("cascade.xml", Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }

        mFaceCascade = CascadeClassifier(context.getFileStreamPath("cascade.xml").absolutePath)
    }

    fun detect(image: Mat, imageName: String = ""): Face?
    {
        val grayImage: Mat
        if (image.channels() == 1)
        {
            grayImage = image
        } else if (image.channels() >= 3)
        {
            grayImage = Mat()
            cvtColor(image, grayImage, COLOR_RGB2GRAY)
        } else
        {
            Log.e(TAG, "Image's channels are not correct.")
            return null
        }

        val faceBoundingBox = findBoundingBox(grayImage) ?: return null
        val faceRect =
                Rect(faceBoundingBox.x, faceBoundingBox.y, faceBoundingBox.w, faceBoundingBox.h)
        val face = Mat(grayImage, faceRect)

        return Face(image.clone(), imageName, face, faceBoundingBox)
    }

    private fun findBoundingBox(imgGray: Mat): Face.BoundingBox?
    {
        val facesRect = RectVector()

        mFaceCascade.detectMultiScale(imgGray, facesRect, 1.25, 5, 0,
                                      Size(MIN_FACE_WIDTH_THRESHOLD, MIN_FACE_WIDTH_THRESHOLD),
                                      Size(4 * MIN_FACE_WIDTH_THRESHOLD,
                                           4 * MIN_FACE_WIDTH_THRESHOLD))

        val faceIndex = when
        {
            facesRect.size() == 1L ->
                if (facesRect.get(0).width() >= MIN_FACE_WIDTH_THRESHOLD) 0L else -1L
            facesRect.size() > 1   ->
            {
                var maxFaceIndex: Long = -1
                var maxFaceWidth = -1
                for (i in 0 until facesRect.size())
                {
                    if (facesRect.get(i).width() > maxFaceWidth)
                    {
                        maxFaceWidth = facesRect.get(i).width()
                        maxFaceIndex = i
                    }
                }
                print(maxFaceWidth)
                if (maxFaceWidth >= MIN_FACE_WIDTH_THRESHOLD) maxFaceIndex else -1
            }
            else                   -> -1
        }

        if (faceIndex < 0)
            return null
        val faceRect = facesRect.get(faceIndex)

        return Face.BoundingBox(faceRect.x(), faceRect.y(), faceRect.width(), faceRect.height())
    }
}
