package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.rd.PageIndicatorView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.SetupAdapter;


public class SetupActivity extends AppCompatActivity
{

    public static final int SETUP_FACE_STEP   = 0;
    public static final int SETUP_VOICE_STEP  = 1;
    public static final int SETUP_FINISH_STEP = 2;

    private ViewPager         mViewPager;
    private PageIndicatorView mPageIndicator;

    public void setCurrentStep(int item)
    {
        mViewPager.setCurrentItem(item);
    }

    public void finishSetup()
    {
        startActivity(new Intent(getApplicationContext(),
                                 SplashScreenActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mViewPager = findViewById(R.id.view_pager);
        mPageIndicator = findViewById(R.id.page_indicator);

        initViews();
    }

    private void initViews()
    {
        SetupAdapter adapter = new SetupAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mPageIndicator.setViewPager(mViewPager);
    }
}
