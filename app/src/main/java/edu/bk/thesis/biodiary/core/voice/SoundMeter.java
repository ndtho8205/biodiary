package edu.bk.thesis.biodiary.core.voice;

import android.media.MediaRecorder;

/**
 * Created by L on 2018/05/08.
 */

public class SoundMeter {

    private MediaRecorder mRecorder = null;

    public void start() throws Exception {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            System.out.println("asdfds");
            return mRecorder.getMaxAmplitude();
        }
        else
            return 0;

    }
}
