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
            Intent intentToStartWelcomeActivity = new Intent(getApplicationContext(),
                                                             SetupActivity.class);
            startActivity(intentToStartWelcomeActivity);
            finish();
            return;
        }

        if (!mPreferencesHandler.isLoggedIn()) {
            Intent intentToStartLoginActivity = new Intent(getApplicationContext(),
                                                           LoginActivity.class);
            startActivity(intentToStartLoginActivity);
            finish();
            return;
        }

        if (mPreferencesHandler.isSetUp() && mPreferencesHandler.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), BioDiaryMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
