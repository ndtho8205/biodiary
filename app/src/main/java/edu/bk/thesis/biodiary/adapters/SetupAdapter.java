package edu.bk.thesis.biodiary.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.bk.thesis.biodiary.fragments.SetupFaceFragment;
import edu.bk.thesis.biodiary.fragments.SetupFinishFragment;
import edu.bk.thesis.biodiary.fragments.SetupVoiceFragment;


public class SetupAdapter extends FragmentPagerAdapter
{

    private static int NUM_SETUP_STEPS = 3;

    public SetupAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return new SetupFaceFragment();
            case 1:
                return new SetupVoiceFragment();
            case 2:
                return new SetupFinishFragment();
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
