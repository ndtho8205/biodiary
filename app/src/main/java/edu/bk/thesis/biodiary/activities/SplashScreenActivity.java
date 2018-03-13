package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.bk.thesis.biodiary.activities.BioDiaryMainActivity;


public class SplashScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BioDiaryMainActivity.class);
        startActivity(intent);
        finish();
    }
}
