package edu.bk.thesis.biodiary.core.voice.wav;

import android.app.ProgressDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class WavRecorder extends Wav
{

    private static final String TAG = WavRecorder.class.getSimpleName();

    private static final short NUM_CHANNELS    = 1;
    private static final int   SAMPLE_RATE     = 44100;
    private static final short BITS_PER_SAMPLE = 16;

    private static final int RECORDER_AUDIO_SOURCE   = AudioSource.MIC;
    private static final int RECORDER_CHANNEL_CONFIG = AudioSource.MIC;
    private static final int RECORDER_AUDIO_FORMAT   = AudioFormat.ENCODING_PCM_16BIT;
    private static final int TIMER_INTERVAL          = 120;

    private final long mStopThreshold  = 1500;
    private       int  mStartThreshold = 350;

    private AudioRecord mAudioRecorder;
    private boolean     mIsRecording;
    private int         mFramePeriod;

    private byte[] mBuffer;
    private int    mBufferSize;

    private State mState;

    private ProgressDialog mSoundLevelDialog;

    public WavRecorder(ProgressDialog soundLevelDialog)
    {
        mSoundLevelDialog = soundLevelDialog;

        mIsRecording = false;

        mSampleRate = SAMPLE_RATE;
        mNumChannels = NUM_CHANNELS;
        mBitsPerSample = BITS_PER_SAMPLE;

        mFramePeriod = mSampleRate * TIMER_INTERVAL / 1000;
        mBufferSize = mFramePeriod * 2 * mBitsPerSample * mNumChannels / 8;
        if (mBufferSize < AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                                                       AudioFormat.ENCODING_PCM_16BIT)) {
            // increase buffer size if needed
            mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                                                       AudioFormat.ENCODING_PCM_16BIT);
            // Set frame period and timer interval accordingly
            mFramePeriod = mBufferSize / (2 * mBitsPerSample * mNumChannels / 8);
            Log.w(TAG, "Increasing buffer size to " + mBufferSize);
        }

        try {
            mAudioRecorder = new AudioRecord(RECORDER_AUDIO_SOURCE,
                                             mSampleRate,
                                             AudioFormat.CHANNEL_IN_MONO,
                                             AudioFormat.ENCODING_PCM_16BIT,
                                             mBufferSize);
            if (mAudioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord initialization failed");
            }
            mAudioRecorder.setPositionNotificationPeriod(mFramePeriod);

            mState = State.INITIALIZING;
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            mState = State.ERROR;
        }
    }

    public State getState()
    {
        return mState;
    }

    public int getStartThreshold()
    {
        return mStartThreshold;
    }

    public void setStartThreshold(int startThreshold)
    {
        mStartThreshold = startThreshold < 1 ? 1 : startThreshold;
    }

    //
    // Primary method
    //

    public void startRecording()
    {
    }

    public void stopRecording()
    {
    }

    public void release()
    {
        if (mState == State.RECORDING) {
            stopRecording();
        }
        else {
            if ((mState == State.READY)) {
                try {
                    mWavFile.close(); // remove prepared file
                }
                catch (IOException e) {
                    Log.e(TAG, "I/O exception occurred while closing output file");
                }
                (new File(mWavFilePath)).delete();
            }
        }

        if (mAudioRecorder != null) {
            mAudioRecorder.release();
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int count)
    {
        System.arraycopy(mBuffer, offset, buffer, 0, count);
        int currentPosition = offset + count;

        return currentPosition;
    }

    public enum State
    {
        INITIALIZING,
        READY,
        RECORDING,
        ERROR,
        STOPPED
    }
}
