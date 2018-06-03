package edu.bk.thesis.biodiary.models

data class VoiceData(val distance: Double,
                     val qualityEnvAmplitude: Double)
{

    override fun toString(): String
    {
        return "$distance;$qualityEnvAmplitude"
    }

}
