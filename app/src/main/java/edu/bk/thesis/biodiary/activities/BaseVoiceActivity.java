package edu.bk.thesis.biodiary.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.bk.thesis.biodiary.core.voice.VoiceAuthenticator;
import edu.bk.thesis.biodiary.core.voice.math.mfcc.FeatureVector;
import edu.bk.thesis.biodiary.core.voice.math.vq.Codebook;
import edu.bk.thesis.biodiary.utils.MessageHelper;


public class BaseVoiceActivity extends AppCompatActivity
{

    private static final String TAG = BaseVoiceActivity.class.getSimpleName();

    protected ProgressDialog mSoundLevelDialog;
    protected ProgressDialog mProcessingDialog;

    protected VerifyTask     mVerifyTask;
    protected List<Codebook> mUserCodebook;

    protected VoiceAuthenticator mVoiceAuthenticator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSoundLevelDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
        mSoundLevelDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSoundLevelDialog.setTitle("Listening...");
        mSoundLevelDialog.setMessage("Say: \"Mở khoá\"");
        mSoundLevelDialog.setCancelable(false);

        mProcessingDialog = new ProgressDialog(this);
        mProcessingDialog.setMessage("Processing...");
        mProcessingDialog.setCancelable(false);

        mVoiceAuthenticator = new VoiceAuthenticator(mSoundLevelDialog);
        try {
            this.mVoiceAuthenticator.setMicThreshold(350);
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Error, Mic threshold not set!");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mSoundLevelDialog.dismiss();
        mProcessingDialog.dismiss();

        // Stop recording
        mVoiceAuthenticator.cancelRecording();
    }

    public ProgressDialog getSoundLevelDialog()
    {
        return mSoundLevelDialog;
    }

    public ProgressDialog getProcessinglDialog()
    {
        return mProcessingDialog;
    }

    public VoiceAuthenticator getVoiceAuthenticator()
    {
        return mVoiceAuthenticator;
    }

    protected float calculateDistances(FeatureVector featureVector)
    {
        ArrayList<Codebook> cb = (ArrayList<Codebook>) mUserCodebook;

        float tempAvgDist = -1.0f;
        if (cb != null) {
            mVoiceAuthenticator.setCodeBook(cb);

            tempAvgDist = mVoiceAuthenticator.identifySpeaker(featureVector);

            if (tempAvgDist == -1f) {
                Log.e(TAG, "Error with calculating feature vector distance for user!");
                return -1f;
            }
            Log.i(TAG, "user average distance = " + tempAvgDist);
        }
        return tempAvgDist;
    }

    protected void startRecording()
    {
        mProcessingDialog.show();
        mSoundLevelDialog.show();

        mVerifyTask = new VerifyTask("login");
        mVerifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected class VerifyTask extends AsyncTask<Void, Void, Void>
    {

        private float mResult;

        public VerifyTask(String filename)
        {
            mVoiceAuthenticator.setOutputFile(filename);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            mVoiceAuthenticator.startRecording();
            mSoundLevelDialog.dismiss();
            mResult = calculateDistances(mVoiceAuthenticator.getCurrentFeatureVector());
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mProcessingDialog.dismiss();

            float bestResult = mResult;
            MessageHelper.showToast(getApplicationContext(),
                                    "Distance: " + bestResult,
                                    Toast.LENGTH_LONG);
        }

    }
}
