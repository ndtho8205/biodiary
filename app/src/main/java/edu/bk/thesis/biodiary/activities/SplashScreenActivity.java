package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class SplashScreenActivity extends AppCompatActivity
{
    static final int SETUP_REQUEST = 1;
    static final int LOGIN_REQUEST = 1;

    PreferencesHandler mPreferencesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        if (!mPreferencesHandler.isSetUp()) {
            Intent intentToStartWelcomeActivity = new Intent(this, WelcomeActivity.class);
            // intentToStartWelcomeActivity.addFlags(
            //         Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivityForResult(intentToStartWelcomeActivity, 10);
        }
        // mPreferencesHandler.checkLogin();


        Intent intent = new Intent(getApplicationContext(), BioDiaryMainActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
