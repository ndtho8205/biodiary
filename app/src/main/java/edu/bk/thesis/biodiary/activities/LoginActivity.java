package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.LoginAdapter;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class LoginActivity extends AppCompatActivity
{
    public static final int LOGIN_FACE_STEP  = 0;
    public static final int LOGIN_VOICE_STEP = 1;

    private ViewPager mViewPager;

    private PreferencesHandler mPreferencesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mViewPager = findViewById(R.id.login_view_pager);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        initViews();
    }

    private void initViews()
    {
        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
    }

    public void setCurrentStep(int item)
    {
        mViewPager.setCurrentItem(item);
    }

    public void finishLogin()
    {
        mPreferencesHandler.createLoginSession();

        Intent intentToStartSplashScreenActivity = new Intent(getApplicationContext(),
                                                              SplashScreenActivity.class);
        intentToStartSplashScreenActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentToStartSplashScreenActivity);
        finish();
    }
}
