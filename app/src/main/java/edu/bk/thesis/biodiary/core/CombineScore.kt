package edu.bk.thesis.biodiary.core

import android.content.Context
import android.util.Log
import edu.bk.thesis.biodiary.models.FaceData
import edu.bk.thesis.biodiary.models.VoiceData
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class CombineScore(context: Context)
{

    private val MODEL_FILE = "file:///android_asset/frozen_mlpThesis.pb"
    private val INPUT_NODES = arrayOf("X:0")
    private val OUTPUT_NODES = arrayOf("y_pred:0")
    private val INPUT_DIM = longArrayOf(1, 512)

    var inferenceInterface: TensorFlowInferenceInterface =
            TensorFlowInferenceInterface(context.assets, MODEL_FILE)

    var inputX = FloatArray(INPUT_DIM[1].toInt())
    var outputYPred = LongArray(1)

    fun predict(faceData: FaceData, voiceData: VoiceData): Boolean
    {
        inputX = floatArrayOf(
                faceData.distance.toFloat(),
                faceData.qualityBrightness.toFloat(),
                faceData.qualityContrast.toFloat(),
                faceData.qualitySharpness.toFloat(),
                voiceData.distance.toFloat(),
                voiceData.qualityEnvAmplitude.toFloat()
                             )
        predict()

        return outputYPred[0] == 1L
    }

    private fun predict()
    {
        inferenceInterface.let {
            it.feed(INPUT_NODES[0], inputX, INPUT_DIM[0], INPUT_DIM[1])
            it.run(OUTPUT_NODES)

            it.fetch(OUTPUT_NODES[0], outputYPred)

            Log.d("OUTPUT", "Predict: ${outputYPred[0]}")
        }
    }

}

