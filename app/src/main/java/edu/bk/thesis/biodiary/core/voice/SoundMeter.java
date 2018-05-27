package edu.bk.thesis.biodiary.core.voice;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;


public class SoundMeter
{

    private MediaRecorder mRecorder = null;

    private boolean mState = false;

    public void start()
    {
        if (mRecorder == null) {

            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                mRecorder.prepare();
                mState = true;
                mRecorder.start();
            }
            catch (IOException e) {
                mState = false;
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        if (mRecorder != null) {
            mState = false;

            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude()
    {

        if (mRecorder != null) {
            int amplitude = mRecorder.getMaxAmplitude();
            Log.d("SoundMeter", "getAmplitude " + amplitude);
            return amplitude;
        }
        else {
            return 0;
        }

    }

    public boolean getState()
    {
        return mState;
    }
}
