package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class WelcomeActivity extends AppCompatActivity
{
    private Button             mDoneButton;
    private PreferencesHandler mPreferencesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        mDoneButton = findViewById(R.id.btn_set_up);
        mDoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPreferencesHandler.setUp();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                finish();
            }
        });
    }
}
