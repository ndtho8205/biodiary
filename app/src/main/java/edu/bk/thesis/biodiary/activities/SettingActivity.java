package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.DiaryAdapter;
import edu.bk.thesis.biodiary.models.Diary;
import edu.bk.thesis.biodiary.utils.DividerItemDecoration;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.*;



public class SettingActivity extends AppCompatActivity
{
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodiary_settings_setting);
        SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        final TextView faceValue = (TextView) findViewById(R.id.fc);
        final TextView voiceValue = (TextView) findViewById(R.id.sc);

        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int value=0;
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                value=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                // TODO Auto-generated method stub
                faceValue.setText("Face Coefficient:"+String.valueOf((double)value/sb.getMax()));
                voiceValue.setText("Face Coefficient:"+String.valueOf(1-(double)value/sb.getMax()));
            }
        });
    }

    private static String getOwnerFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "owner" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    public void saveCoefficent(View arg0){
        SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        double faceCo= (double)sb.getProgress()/sb.getMax();
        double voiceCo= 1-faceCo;
        //TO-DO
        //SAVE NHOE THO
        //SAVE 2 CAI faceCo va voiceCo
        finish();

    }

    public void cancel(View arg0){
        finish();
    }

    public void deleteData(View v){
        //Delete voice data
        File file = new File(getOwnerFilename());
        file.delete();

        //Delete face data
        //Hung gank tem
    }
}
