package edu.bk.thesis.biodiary.handlers;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHandler
{
    public static final  String KEY_NAME     = "name";
    private static final String PREF_NAME    = "BioDiary";
    private static final String IS_SETUP     = "isSetUp";
    private static final String IS_LOGIN     = "isLoggedIn";
    private static final int    PRIVATE_MODE = Context.MODE_PRIVATE;

    private SharedPreferences        mPref;
    private SharedPreferences.Editor mEditor;
    private Context                  mContext;

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

    public void logoutUser()
    {
        mEditor.putBoolean(IS_LOGIN, false);
        mEditor.commit();
    }

    public void reset()
    {
        mEditor.putBoolean(IS_SETUP, false);
        mEditor.putBoolean(IS_LOGIN, false);
        mEditor.commit();
    }

    public boolean isSetUp()
    {
        return mPref.getBoolean(IS_SETUP, false);
    }

    public boolean isLoggedIn()
    {
        return mPref.getBoolean(IS_LOGIN, false);
    }
}
