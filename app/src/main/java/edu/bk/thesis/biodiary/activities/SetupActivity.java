package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.SetupAdapter;


public class SetupActivity extends AppCompatActivity
{
    private ViewPager         mViewPager;
    private PageIndicatorView mPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initViews();
    }

    private void initViews()
    {
        SetupAdapter adapter = new SetupAdapter(getSupportFragmentManager());

        final ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(adapter);

        mPageIndicator = findViewById(R.id.page_indicator);
        mPageIndicator.setAnimationType(AnimationType.THIN_WORM);
        mPageIndicator.setViewPager(pager);
    }

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
}
