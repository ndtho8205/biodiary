package edu.bk.thesis.biodiary.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class SettingsActivity extends AppCompatActivity
{

    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER       = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE    = "record_temp.raw";

    private SeekBar  mCoefficientSeekbar;
    private TextView mFaceCoefficientTextView;
    private TextView mVoiceCoefficientTextView;

    private float mFaceCoefficient;
    private float mVoiceCoefficient;

    private PreferencesHandler mPreferencesHandler;

    private static String getOwnerFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File   file     = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "owner" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    public void deleteData(View v)
    {
        //Delete voice data
        File file = new File(getOwnerFilename());
        file.delete();

        //Delete face data
        //Hung gank tem
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCoefficientSeekbar = findViewById(R.id.seekbar_coefficient);
        mFaceCoefficientTextView = findViewById(R.id.face_coefficient_value);
        mVoiceCoefficientTextView = findViewById(R.id.voice_coefficient_value);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());
        mFaceCoefficient = mPreferencesHandler.getFaceCoefficient();
        mVoiceCoefficient = mPreferencesHandler.getVoiceCoefficient();

        mCoefficientSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser)
            {
                handleSeekbarChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb)
            {
                // TODO Auto-generated method stub
            }
        });

        mCoefficientSeekbar.setProgress((int) (mFaceCoefficient * 100));
    }

    private void handleSeekbarChange(int value)
    {
        mFaceCoefficient = value / (float) mCoefficientSeekbar.getMax();
        mVoiceCoefficient = 1.0f - mFaceCoefficient;

        mFaceCoefficientTextView.setText(float2String(mFaceCoefficient));
        mVoiceCoefficientTextView.setText(float2String(mVoiceCoefficient));

        mPreferencesHandler.updateCoefficients(mFaceCoefficient, mVoiceCoefficient);
    }

    private String float2String(float value)
    {
        return new DecimalFormat("#.##").format(value);
    }
}

