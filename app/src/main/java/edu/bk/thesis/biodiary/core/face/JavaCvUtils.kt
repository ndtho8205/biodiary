package edu.bk.thesis.biodiary.core.face

import org.bytedeco.javacpp.opencv_core
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_imgcodecs.imread
import org.bytedeco.javacpp.opencv_imgcodecs.imwrite
import org.bytedeco.javacpp.opencv_imgproc.*
import java.io.File
import java.io.FilenameFilter

object JavaCvUtils
{

    fun imreadGray(imagePath: String): Mat
    {
        val img = imread(imagePath)
        cvtColor(img, img, COLOR_BGR2GRAY)
        return img
    }

    fun getAllFilesInDirectory(filesDir: String): Array<File>?
    {
        val root = File(filesDir)

        val imageFilter = FilenameFilter { _, name ->
            name.toLowerCase().endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".pgm")
        }

        return root.listFiles(imageFilter)
    }

    fun list2MatVector(list: List<Mat>): MatVector?
    {
        if (list.isEmpty())
            return null

        val matVector = MatVector(list.size.toLong())
        for ((counter, mat) in list.withIndex())
        {
            matVector.put(counter.toLong(), mat)
        }
        return matVector
    }

    fun imsave(path: String, image: Mat)
    {
        imwrite(path, image)
    }

    fun showDetectedFace(face: Face, image: Mat)
    {
        val x = face.boundingBox.x
        val y = face.boundingBox.y
        val w = face.boundingBox.w
        val h = face.boundingBox.h

        rectangle(image,
                  opencv_core.Point(x, y),
                  opencv_core.Point(x + w, y + h),
                  opencv_core.Scalar.YELLOW,
                  2,
                  LINE_8,
                  0)
    }
}
