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


public class RecordVoiceActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivy_record_sound);
    }


    public void onClick(View view)
    {
        System.out.println("It worked!!!");
    }

}
