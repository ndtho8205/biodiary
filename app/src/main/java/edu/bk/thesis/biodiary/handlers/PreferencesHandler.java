package edu.bk.thesis.biodiary.handlers;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHandler
{

    public static final String KEY_USER_IMAGES             = "userImages";
    public static final String KEY_IS_SETUP                = "isSetUp";
    public static final String KEY_IS_LOGIN                = "isLoggedIn";
    public static final String KEY_FACE_THRESHOLD          = "faceThreshold";
    public static final String KEY_FACE_DISTANCE_THRESHOLD = "faceDistanceThreshold";
    public static final String KEY_FACE_COEFFICIENT        = "faceCoefficient";
    public static final String KEY_VOICE_COEFFICIENT       = "voiceCoefficient";

    private static final String PREF_NAME    = "BioDiary";
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
        mEditor.putBoolean(KEY_IS_LOGIN, true);
        mEditor.commit();
    }

    public void setUp()
    {
        mEditor.putBoolean(KEY_IS_SETUP, true);
        mEditor.commit();
    }

    public void logoutUser()
    {
        mEditor.putBoolean(KEY_IS_LOGIN, false);
        mEditor.commit();
    }

    public void reset()
    {
        mEditor.clear().commit();
    }

    public boolean isSetUp()
    {
        return mPref.getBoolean(KEY_IS_SETUP, false);
    }

    public boolean isLoggedIn()
    {
        return mPref.getBoolean(KEY_IS_LOGIN, false);
    }

    public void updateCoefficients(float faceCoefficient, float voiceCoefficient)
    {
        mEditor.putFloat(KEY_FACE_COEFFICIENT, faceCoefficient);
        mEditor.putFloat(KEY_VOICE_COEFFICIENT, voiceCoefficient);
        mEditor.commit();
    }

    public float getFaceCoefficient()
    {
        return mPref.getFloat(KEY_FACE_COEFFICIENT, 0.5f);
    }

    public float getVoiceCoefficient()
    {
        return mPref.getFloat(KEY_VOICE_COEFFICIENT, 0.5f);
    }

    public float getFaceThreshold()
    {
        return mPref.getFloat(KEY_FACE_THRESHOLD, -1.0f);
    }

    public float getFaceDistanceThreshold()
    {
        return mPref.getFloat(KEY_FACE_DISTANCE_THRESHOLD, -1.0f);
    }
}
