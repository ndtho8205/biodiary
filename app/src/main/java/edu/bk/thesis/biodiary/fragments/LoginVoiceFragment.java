package edu.bk.thesis.biodiary.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
            mSoundLevelDialog.setMessage("Say: \"My voice is my password, and it should log me in\"");

            startRecording();
        }
    }

    private void startRecording()
    {
        mProcessingDialog.show();
        mSoundLevelDialog.show();

        SoundMeter soundMeter = new SoundMeter();
        double     amplitude  = 0.0;
        try {
            MessageHelper.showToast(getActivity(),
                                    "Please keep silence in 3 seconds!",
                                    Toast.LENGTH_SHORT);

            soundMeter.start();
            soundMeter.getAmplitude();
            Thread.sleep(3000);
            amplitude = soundMeter.getAmplitude();
            soundMeter.stop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mVerifyTask = new VerifyTask("login", amplitude);
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

        private float  mResult;
        private double mAmplitude;

        public VerifyTask(String filename, double amplitude)
        {
            mAmplitude = amplitude;
            mVoiceAuthenticator.setOutputFile(
                filename + "_" + System.currentTimeMillis() + "_" + amplitude);
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
            MessageHelper.showToast(getActivity(), "Please press LOGIN button.", Toast.LENGTH_LONG);

            mOnLoginVoiceCallbackReceived.updateVoiceData(new VoiceData(bestResult / 250.0,
                                                                        mAmplitude / 32767.0));
        }
    }
}
