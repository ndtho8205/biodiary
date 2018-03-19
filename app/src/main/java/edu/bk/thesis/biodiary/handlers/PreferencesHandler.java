package edu.bk.thesis.biodiary.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import edu.bk.thesis.biodiary.activities.LoginActivity;
import edu.bk.thesis.biodiary.activities.WelcomeActivity;


public class PreferencesHandler
{
    public static final  String KEY_NAME     = "name";
    private static final String PREF_NAME    = "BioDiary";
    private static final String IS_SETUP     = "isSetUp";
    private static final String IS_LOGIN     = "isLoggedIn";
    private static final int    PRIVATE_MODE = 0;

    SharedPreferences        mPref;
    SharedPreferences.Editor mEditor;
    Context                  mContext;

    public PreferencesHandler(Context context)
    {
        mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void createLoginSession()
    {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.commit();
    }

    public void setUp()
    {
        mEditor.putBoolean(IS_SETUP, true);
        mEditor.commit();
    }

    public void checkSetup()
    {
        if (!this.isSetUp()) {
            Intent intentToStartWelcomeActivity = new Intent(mContext, WelcomeActivity.class);
            intentToStartWelcomeActivity.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intentToStartWelcomeActivity);
        }
    }

    public boolean isSetUp()
    {
        return mPref.getBoolean(IS_SETUP, false);
    }

    public void checkLogin()
    {
        if (!this.isLoggedIn()) {
            Intent intentToStartLoginActivity = new Intent(mContext, LoginActivity.class);
            intentToStartLoginActivity.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intentToStartLoginActivity);
        }
    }

    public boolean isLoggedIn()
    {
        return mPref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser()
    {
        mEditor.putBoolean(IS_LOGIN, false);
        mEditor.commit();

        Intent intentToStartLoginActivity = new Intent(mContext, LoginActivity.class);
        intentToStartLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentToStartLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentToStartLoginActivity);
    }
}
