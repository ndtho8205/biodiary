package edu.bk.thesis.biodiary.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.bk.thesis.biodiary.fragments.LoginFaceFragment;
import edu.bk.thesis.biodiary.fragments.LoginVoiceFragment;


public class LoginAdapter extends FragmentPagerAdapter
{

    private static int NUM_SETUP_STEPS = 2;

    public LoginAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return new LoginFaceFragment();
            case 1:
                return new LoginVoiceFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return NUM_SETUP_STEPS;
    }
}

