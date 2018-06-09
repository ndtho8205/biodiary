package edu.bk.thesis.biodiary.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.LoginAdapter;
import edu.bk.thesis.biodiary.core.CombineScore;
import edu.bk.thesis.biodiary.fragments.LoginFaceFragment;
import edu.bk.thesis.biodiary.fragments.LoginVoiceFragment;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;
import edu.bk.thesis.biodiary.models.FaceData;
import edu.bk.thesis.biodiary.models.VoiceData;
import edu.bk.thesis.biodiary.utils.MessageHelper;


public class LoginActivity extends AppCompatActivity
    implements LoginFaceFragment.OnLoginFaceCallbackReceived,
               LoginVoiceFragment.OnLoginVoiceCallbackReceived
{

    public static final int LOGIN_FACE_STEP  = 0;
    public static final int LOGIN_VOICE_STEP = 1;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView (R.id.login_view_pager)
    ViewPager mViewPager;

    private PreferencesHandler mPreferencesHandler;

    private FaceData  mFaceData;
    private VoiceData mVoiceData;

    private LoginTask mLoginTask;
    private LoginTask.Callback mLoginTaskCallback = new LoginTask.Callback()
    {
        @Override
        public void onLoginComplete(final boolean result)
        {
            Log.d(TAG, "Login: " + result);

            AlertDialog alertDial7  og = new AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.setTitle("Login");
            alertDialog.setMessage("Login result: " + result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                  new DialogInterface.OnClickListener()
                                  {
                                      public void onClick(DialogInterface dialog, int which)
                                      {
                                          dialog.dismiss();
                                          if (result) {
                                              finishLogin();
                                          }
                                      }
                                  });
            alertDialog.show();
        }
    };

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

    @Override
    public void updateFaceData(FaceData faceData)
    {
        Log.d(TAG, "Face: " + faceData.toString());
        mFaceData = faceData;
    }

    @Override
    public void updateVoiceData(VoiceData voiceData)
    {
        Log.d(TAG, "Voice: " + voiceData.toString());
        mVoiceData = voiceData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        initViews();
    }

    @OnClick (R.id.btn_reset)
    void onResetButtonPressed()
    {
        mPreferencesHandler.reset();

        Intent intentToStartSplashScreenActivity = new Intent(getApplicationContext(),
                                                              SplashScreenActivity.class);
        intentToStartSplashScreenActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentToStartSplashScreenActivity);
        finish();
    }

    @OnClick (R.id.btn_login)
    void onLoginButtonPressed()
    {
        if (mFaceData != null && mVoiceData != null) {
            if (mLoginTask != null && mLoginTask.getStatus() != AsyncTask.Status.FINISHED) {
                Log.i(TAG, "Login task is still running");
            }
            else {
                mLoginTask = new LoginTask(this, mFaceData, mVoiceData, mLoginTaskCallback);
                mLoginTask.execute();
            }
        }
        else {
            MessageHelper.showToast(this,
                                    "Cannot login. Take picture/record voice again.",
                                    Toast.LENGTH_LONG);
        }
    }

    private void initViews()
    {
        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
    }

    static class LoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private static final String TAG = "LoginTask";

        private FaceData     mFaceData;
        private VoiceData    mVoiceData;
        private CombineScore mCombineScore;

        private ProgressDialog mDialog;
        private Callback       mCallback;

        LoginTask(Context context, FaceData faceData, VoiceData voiceData, Callback callback)
        {
            mFaceData = faceData;
            mVoiceData = voiceData;
            mCombineScore = new CombineScore(context);

            mDialog = new ProgressDialog(context);
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            Log.d(TAG, "Start login...");
            try {
                boolean result = mCombineScore.predict(mFaceData, mVoiceData);
                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            mDialog.setMessage("Login in progress...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            Log.d(TAG, "Login completed!");

            mCallback.onLoginComplete(aBoolean);

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        interface Callback
        {

            void onLoginComplete(boolean result);
        }
    }
}
