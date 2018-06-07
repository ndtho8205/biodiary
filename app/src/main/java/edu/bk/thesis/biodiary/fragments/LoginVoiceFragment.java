package edu.bk.thesis.biodiary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.voice.SoundMeter;
import edu.bk.thesis.biodiary.core.voice.math.mfcc.FeatureVector;
import edu.bk.thesis.biodiary.core.voice.math.vq.Codebook;
import edu.bk.thesis.biodiary.models.VoiceData;
import edu.bk.thesis.biodiary.utils.MessageHelper;
import edu.bk.thesis.biodiary.utils.SerializeArray;
import edu.bk.thesis.biodiary.utils.StorageHelper;


public class LoginVoiceFragment extends BaseVoiceFragment
{

    private static final String TAG = LoginVoiceFragment.class.getSimpleName();

    private final int maxNumAttempts = 3;

    @BindView (R.id.login_voice_pb_record)
    ProgressBar mRecordView;

    private VerifyTask mVerifyTask;

    private List<Codebook> mUserCodebook;

    private OnLoginVoiceCallbackReceived mOnLoginVoiceCallbackReceived;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mOnLoginVoiceCallbackReceived = (OnLoginVoiceCallbackReceived) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login_voice, container, false);

        ButterKnife.bind(this, view);

        mUserCodebook
            = SerializeArray.INSTANCE.loadArray(StorageHelper.INSTANCE.retrieveVoiceModelPath(
            "codebook.yml"));

        mSoundLevelDialog.setMessage("Say: \"The quick brown fox jumps over the lazy dog\"");

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mVerifyTask != null) {
            mVerifyTask.cancel(true);
        }
    }

    @OnClick (R.id.login_voice_pb_record)
    void identifySpeaker()
    {
        if (mVerifyTask != null &&
            mVerifyTask.getStatus() != AsyncTask.Status.FINISHED) {
            MessageHelper.showToast(getActivity(),
                                    "Compute feature still running.",
                                    Toast.LENGTH_SHORT);
        }
        else {

            Log.d(TAG, "Identifying Voice");

            startRecording();
        }
    }

    private void startRecording()
    {
        mProcessingDialog.show();

        mVerifyTask = new VerifyTask(getContext(), "login", mSoundLevelDialog);
        mVerifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private float calculateDistances(FeatureVector featureVector)
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

    public interface OnLoginVoiceCallbackReceived
    {

        void updateVoiceData(VoiceData voiceData);
    }

    private class VerifyTask extends AsyncTask<Void, Void, Void>
    {

        private Context        mContext;
        private String         mFilename;
        private ProgressDialog mSoundLevelDialog;

        private float  mResult;
        private double mAmplitude;

        public VerifyTask(Context context, String filename, ProgressDialog soundLevelDialog)
        {
            mContext = context;
            mFilename = filename;
            mSoundLevelDialog = soundLevelDialog;

        }

        @Override
        protected Void doInBackground(Void... params)
        {
            SoundMeter soundMeter = new SoundMeter();
            double     amplitude  = 0.0;
            try {
                Handler handler = new Handler(mContext.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        MessageHelper.showToast(mContext,
                                                "Please keep silence in 3 seconds!",
                                                Toast.LENGTH_LONG);
                    }
                });

                soundMeter.start();
                soundMeter.getAmplitude();
                Thread.sleep(3000);
                amplitude = soundMeter.getAmplitude();
                soundMeter.stop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            mAmplitude = amplitude;

            mVoiceAuthenticator.setOutputFile(
                mFilename + "_" + System.currentTimeMillis() + "_" + amplitude);

            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable()
            {
                public void run()
                {
                    MessageHelper.showToast(mContext,
                                            "Clearly read the phrase out loud.",
                                            Toast.LENGTH_LONG);
                    mSoundLevelDialog.show();
                }
            });

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
            MessageHelper.showToast(getActivity(), "Please press LOGIN button.", Toast.LENGTH_LONG);

            mOnLoginVoiceCallbackReceived.updateVoiceData(new VoiceData(bestResult / 250.0,
                                                                        mAmplitude / 32767.0));
        }
    }
}
