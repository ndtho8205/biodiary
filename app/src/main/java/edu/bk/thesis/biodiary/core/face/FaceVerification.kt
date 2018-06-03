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

class FaceVerification
{

    companion object
    {

        const val FACE_IMAGE_QUANTITY = 25
    }

    private val TAG = "FaceVerification"

    private val MODEL_FILENAME = "model_face.yml"

    private val mRecognizer = createEigenFaceRecognizer(128, 10000.0)

    private var mIsTrained = false

    fun loadTrainedModel()
    {
        mIsTrained = try
        {
            val modelPath = StorageHelper.retrieveFaceModelPath(MODEL_FILENAME)
            mRecognizer.load(modelPath)
            Log.i(TAG, "Face model loaded")
            true
        } catch (e: Exception)
        {
            e.printStackTrace()
            false
        }
    }

    fun saveTrainedModel()
    {
        val modelPath = StorageHelper.retrieveFaceModelPath(MODEL_FILENAME)
        mRecognizer.save(modelPath)
        Log.i(TAG, "Face model saved")
    }

    fun train(faces: List<Face>): Boolean
    {
        return try
        {
            val faceImageList = faces.map { it.alignedFaceImage; }
            if (faceImageList.isEmpty())
                return false

            val labels = generateLabels(faces)

            mRecognizer.train(JavaCvUtils.list2MatVector(faceImageList), labels)
            true
        } catch (e: Exception)
        {
            e.printStackTrace()
            false
        }
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

    fun predict(testFace: Face): Double
    {
        return try
        {
            val label = IntPointer(1)
            val confidence = DoublePointer(1)

            mRecognizer.predict(testFace.alignedFaceImage, label, confidence)

            val predictedLabel = label.get(0)
            val predictedConfidence = confidence.get(0)

            Log.d(TAG, "Face: ${testFace.faceImageName}")
            Log.d(TAG, "Predicted label: $predictedLabel")
            Log.d(TAG, "Confidence: $predictedConfidence")

            predictedConfidence
        } catch (e: Exception)
        {
            e.printStackTrace()
            -1.0
        }
    }

    fun predict(testFaces: List<Face>): List<Double>
    {
        return testFaces.map {
            predict(it)
        }
    }

    class PredictTask(context: Context, private val face: Face, private val callback: Callback?) :
            AsyncTask<Void, Void, Boolean>()
    {

        val TAG = "PredictTask"

        private val mDialog = ProgressDialog(context)
        private val verifier = FaceVerification()
        private var mDistance = 7000.0

        interface Callback
        {

            fun onPredictComplete(result: Boolean, face: Face, distance: Double)
        }

        override fun onPreExecute()
        {
            super.onPreExecute()

            mDialog.setMessage("Computing...")
            mDialog.setCancelable(false)
            mDialog.show()
        }

        override fun doInBackground(vararg params: Void?): Boolean
        {
            verifier.loadTrainedModel()

            Log.d(TAG, "Start predicting...")

            mDistance = verifier.predict(face)

            return mDistance >= 0.0
        }

        override fun onPostExecute(result: Boolean?)
        {
            Log.d(TAG, "Predict completed!")

            callback?.onPredictComplete(result ?: false, face, mDistance)
            if (mDialog.isShowing)
            {
                mDialog.dismiss()
            }
        }
    }

    class TrainTask(context: Context,
                    private val faces: List<Face>,
                    private val callback: Callback?) :
            AsyncTask<Void, Void, Boolean>()
    {

        val TAG = "TrainTask"

        private val mDialog = ProgressDialog(context)
        private val verifier = FaceVerification()

        interface Callback
        {

            fun onTrainComplete(result: Boolean)
        }

        override fun onPreExecute()
        {
            mDialog.setMessage("Training...")
            mDialog.setCancelable(false)
            mDialog.show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): Boolean
        {
            Log.d(TAG, "Start training...")
            Log.d(TAG, "Number of training faces: " + faces.size)

            return verifier.train(faces)
        }

        override fun onPostExecute(result: Boolean?)
        {
            Log.d(TAG, "Training completed!")
            if (result == true)
            {
                verifier.saveTrainedModel()
            }
            callback?.onTrainComplete(result ?: false)
            if (mDialog.isShowing)
            {
                mDialog.dismiss()
            }
        }
    }
}
