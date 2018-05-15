package edu.bk.thesis.biodiary.core.face

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import edu.bk.thesis.biodiary.utils.StorageHelper
import org.bytedeco.javacpp.DoublePointer
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.opencv_core.CV_32SC1
import org.bytedeco.javacpp.opencv_core.Mat
import org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer
import java.nio.IntBuffer

object Verification
{

    const val TAG = "Verification"

    const val FACE_IMAGE_QUANTITY = 25
    const val MODEL_FILENAME = "model.yml"

    private val mRecognizer = createEigenFaceRecognizer(128, 10000.0)

    fun load(context: Context)
    {
        val modelPath = StorageHelper.retrievePrivatePath(context, MODEL_FILENAME)
        mRecognizer.load(modelPath)
    }

    fun save(context: Context)
    {
        val modelPath = StorageHelper.retrievePrivatePath(context, MODEL_FILENAME)
        mRecognizer.save(modelPath)
    }

    fun train(faces: List<Face>): Boolean
    {
        val labels = generateLabels(faces)
        val faceImageList = faces.map { it.alignedImage; }
        if (faceImageList.isEmpty())
            return false
        mRecognizer.train(JavaCvUtils.list2MatVector(faceImageList), labels)
        return true
    }

    private fun generateLabels(faces: List<Face>): Mat
    {
        val labels = Mat(faces.size, 1, CV_32SC1)
        val label = 100
        val labelsBuf: IntBuffer = labels.createBuffer()

        for ((counter, _) in faces.withIndex())
        {
            labelsBuf.put(counter, label)
        }

        return labels
    }

    fun predict(testFace: Face, callback: PredictTask.Callback?)
    {
        Log.d(TAG, "Start predicting a face...")
        val label = IntPointer(1)
        val confidence = DoublePointer(1)
        mRecognizer.predict(testFace.alignedImage, label, confidence)

        val predictedLabel = label.get(0)
        val predictedConfidence = confidence.get(0)
        Log.d(TAG, "Face: ${testFace.containerImageName}")
        Log.d(TAG, "Predicted label: $predictedLabel")
        Log.d(TAG, "Confidence: $predictedConfidence")
        callback?.onPredictComplete(predictedConfidence)
    }

    fun predict(testFaces: List<Face>, callback: PredictTask.Callback? = null)
    {
        testFaces.forEach {
            predict(it, callback)
        }
    }

    object PredictTask
    {

        interface Callback
        {

            fun onPredictComplete(confidence: Double)
        }
    }

    class TrainTask(private val context: Context, private val faces: List<Face>,
                    private val callback: Callback?) :
            AsyncTask<Void, Void, Boolean>()
    {
        private val mDialog = ProgressDialog(context)

        interface Callback
        {

            fun onTrainComplete(result: Boolean)
        }

        override fun onPreExecute()
        {
            mDialog.setMessage("Training...")
            mDialog.show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): Boolean
        {
            Log.d(Verification.TAG, "Start training...")
            Log.d(Verification.TAG, "Number of training faces: " + faces.size)
            return try
            {
                return Verification.train(faces)
            } catch (e: Exception)
            {
                Log.e(TAG, e.message, e)
                false
            }
        }

        override fun onPostExecute(result: Boolean?)
        {
            Log.d(Verification.TAG, "Training completed!")
            callback?.onTrainComplete(result ?: false)
            if (mDialog.isShowing)
            {
                mDialog.dismiss()
            }
        }
    }
}
