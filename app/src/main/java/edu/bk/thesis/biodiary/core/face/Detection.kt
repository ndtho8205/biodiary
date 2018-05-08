package edu.bk.thesis.biodiary.core.face

import android.content.Context
import android.util.Log
import edu.bk.thesis.biodiary.R
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier

object Detection
{

    private const val TAG = "Detection"
    private const val MIN_FACE_WIDTH_THRESHOLD = 200

    private var mFaceCascade: CascadeClassifier? = null

    fun load(context: Context)
    {
        val inputStream = context.resources.openRawResource(R.raw.haarcascade_frontalface_alt)
        val content = inputStream.bufferedReader().use { it.readText() }

        context.openFileOutput("cascade.xml", Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }

        mFaceCascade = CascadeClassifier(context.getFileStreamPath("cascade.xml").absolutePath)
    }

    private fun findBoundingBox(imgGray: Mat): Face.BoundingBox?
    {
        val facesRect = RectVector()

        mFaceCascade?.detectMultiScale(imgGray, facesRect)
        Log.d(TAG, "Number of face detected in images: " + facesRect.size())

        val faceIndex = when
        {
            facesRect.size() == 1L -> if (facesRect.get(
                            0).width() > MIN_FACE_WIDTH_THRESHOLD) 0L else -1L
            facesRect.size() > 1   ->
            {
                Log.d(TAG, "More than one face detected.")
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
                if (maxFaceWidth > MIN_FACE_WIDTH_THRESHOLD) maxFaceIndex else -1
            }
            else                   ->
            {
                Log.d(TAG, "No face detected.")
                -1
            }
        }

        if (faceIndex < 0)
            return null
        val faceRect = facesRect.get(faceIndex)

        return Face.BoundingBox(faceRect.x(), faceRect.y(), faceRect.width(), faceRect.height())
    }

    fun detect(imgGray: Mat, imageName: String = ""): Face?
    {
        val faceBoundingBox = findBoundingBox(imgGray) ?: return null
        val faceRect =
                Rect(faceBoundingBox.x, faceBoundingBox.y, faceBoundingBox.w, faceBoundingBox.h)
        val face = Mat(imgGray, faceRect)

        return Face(face, faceBoundingBox, imageName)
    }
}
