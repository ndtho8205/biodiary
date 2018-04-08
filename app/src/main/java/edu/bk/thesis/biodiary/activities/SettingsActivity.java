package edu.bk.thesis.biodiary.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class SettingsActivity extends AppCompatActivity
{

    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER       = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE    = "record_temp.raw";

    private PreferencesHandler mPreferencesHandler;

    public void saveCoefficent(View arg0) throws Exception
    {
        SeekBar sb      = (SeekBar) findViewById(R.id.seekBar);
        float   faceCo  = (float) sb.getProgress() / sb.getMax();
        float   voiceCo = 1 - faceCo;

        mPreferencesHandler.updateCoefficients(faceCo, voiceCo);
        finish();
    }

    public void cancel(View arg0)
    {
        finish();
    }

    public void deleteData(View v)
    {
        //Delete voice data
        File file = new File(getOwnerFilename());
        file.delete();

        //Delete face data
        //Hung gank tem
    }

    private static String getOwnerFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File   file     = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "owner" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SeekBar        sb         = (SeekBar) findViewById(R.id.seekBar);
        final TextView faceValue  = (TextView) findViewById(R.id.fc);
        final TextView voiceValue = (TextView) findViewById(R.id.sc);

        try {
            double faceCo = mPreferencesHandler.getFaceCoefficient();
            sb.setProgress((int) (faceCo * 100));
            faceValue.setText("Face Coefficient:" + (Double.toString(faceCo)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            double voiceCo = mPreferencesHandler.getVoiceCoefficient();
            voiceValue.setText("Voice Coefficient:" + (Double.toString(voiceCo)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int value = 0;

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser)
            {
                // TODO Auto-generated method stub
                value = progress;
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
                faceValue.setText(
                        "Face Coefficient:" + (String.valueOf((double) value / sb.getMax())));
                voiceValue.setText("Voice Coefficient:" +
                                   (String.valueOf(1 - (double) value / sb.getMax())));
            }
        });
    }
}

