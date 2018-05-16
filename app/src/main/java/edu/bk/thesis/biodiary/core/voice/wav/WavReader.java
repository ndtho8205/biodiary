package edu.bk.thesis.biodiary.core.voice.wav;

import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;


public class WavReader extends Wav
{

    private static final String TAG = WavReader.class.getSimpleName();

    private long mCurrentPosition;

    public WavReader(String filePath)
    {
        init(filePath);
    }

    @Override
    public int read(byte[] buffer, int offset, int count) throws IOException
    {
        int read = mWavFile.read(buffer, offset, count);
        mCurrentPosition += read;
        mWavFile.seek(mCurrentPosition);
        return read;
    }

    public void open(String filePath)
    {
        init(filePath);
    }

    public void close()
    {
        try {
            mWavFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(String filePath)
    {
        try {
            mWavFile = new RandomAccessFile(filePath, "r");

            // read file size and payload size (data) fields
            mWavFile.seek(4);
            mFileSize = Integer.reverseBytes(mWavFile.readInt());
            mWavFile.seek(40);
            mPayloadSize = Integer.reverseBytes(mWavFile.readInt());
            Log.d(TAG, "File size: " + mFileSize);
            Log.d(TAG, "Payload size: " + mPayloadSize);

            // get other metadata
            mWavFile.seek(22);
            mNumChannels = Short.reverseBytes(mWavFile.readShort());
            mSampleRate = Integer.reverseBytes(mWavFile.readInt());
            mByteRate = Integer.reverseBytes(mWavFile.readInt());
            mBlockAlign = Short.reverseBytes(mWavFile.readShort());
            mBitsPerSample = Short.reverseBytes(mWavFile.readShort());
            Log.d(TAG, "Number of channels: " + mNumChannels);
            Log.d(TAG, "Sample rate: " + mSampleRate);
            Log.d(TAG, "Byte rate: " + mByteRate);
            Log.d(TAG, "Frame size: " + mBlockAlign);

            // set at start of data part
            mCurrentPosition = 44;
            mWavFile.seek(mCurrentPosition);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
