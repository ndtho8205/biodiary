package edu.bk.thesis.biodiary.models

data class FaceData(val distance: Double,
                    val qualityBrightness: Double,
                    val qualityContrast: Double,
                    val qualitySharpness: Double)
{

    override fun toString(): String
    {
        return "$distance;$qualityBrightness;$qualityContrast;$qualitySharpness"
    }

}
