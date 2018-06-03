package edu.bk.thesis.biodiary.core.face;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;


public class FaceQualityComputation
{

    // Calculate brightness of image based on YUV color space
    public float computeBrightnessScore(Mat rgbaMat)
    {
        float brightness;

        UByteRawIndexer brightElem = rgbaMat.createIndexer();
        float              sum        = 0;
        for (int x = 0; x < rgbaMat.rows(); x++) {
            for (int y = 0; y < rgbaMat.cols(); y++) {
                int   r = brightElem.get(x, y, 2);
                int   g = brightElem.get(x, y, 1);
                int   b = brightElem.get(x, y, 0);
                float Y = (float) (0.299 * r + 0.587 * g + 0.114 * b);
                sum += Y;
            }
        }
        brightness = sum / (rgbaMat.rows() * rgbaMat.cols());

        return brightness;
    }

    // Calculate contrast of image using the Root Mean Square (RMS) equation
    public float computeContrastScore(Mat grayMat)
    {
        float contrast;

        UByteRawIndexer contrastElem = grayMat.createIndexer();
        float              mean         = 0;
        for (int x = 0; x < grayMat.rows(); x++) {
            for (int y = 0; y < grayMat.cols(); y++) {
                mean += contrastElem.get(x, y);
            }
        }
        mean /= (grayMat.rows() * grayMat.cols());

        float temp = 0;
        for (int x = 0; x < grayMat.rows(); x++) {
            for (int y = 0; y < grayMat.cols(); y++) {
                temp += Math.pow(contrastElem.get(x, y) - mean, 2);
            }
        }
        contrast = (float) Math.sqrt(temp / (grayMat.rows() * grayMat.cols()));

        return contrast;
    }

    //Calculate sharpness of image
    // Based on research of Kryszczuk and Drygajlo (Paper name: "On combining evidence for reliability estimation in face verification")
    public float computeSharpnessScore(Mat grayMat)
    {
        float sharpness;

        UByteRawIndexer sharpElem = grayMat.createIndexer();

        float sumX = 0;
        for (int x = 0; x < grayMat.rows(); x++) {
            for (int y = 0; y < grayMat.cols() - 1; y++) {
                sumX += Math.abs(sharpElem.get(x, y) - sharpElem.get(x, y + 1));
            }
        }
        sumX /= grayMat.rows() * (grayMat.cols() - 1);

        float sumY = 0;
        for (int x = 0; x < grayMat.rows() - 1; x++) {
            for (int y = 0; y < grayMat.cols(); y++) {
                sumY += Math.abs(sharpElem.get(x, y) - sharpElem.get(x + 1, y));
            }
        }
        sumY /= grayMat.cols() * (grayMat.rows() - 1);

        sharpness = (float) (0.5 * (sumX + sumY));

        return sharpness;
    }

}
