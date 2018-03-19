package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class LoginActivity extends AppCompatActivity
{
    private Button             mDoneButton;
    private PreferencesHandler mPreferencesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        mDoneButton = findViewById(R.id.btn_logged_in);
        mDoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPreferencesHandler.createLoginSession();

                Intent intentToStartSplashScreenActivity = new Intent(getApplicationContext(),
                                                                      SplashScreenActivity.class);
                intentToStartSplashScreenActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentToStartSplashScreenActivity);
                finish();
            }
        });
    }
}
