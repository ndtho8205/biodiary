package edu.bk.thesis.biodiary.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.voice.Constants;
import edu.bk.thesis.biodiary.core.voice.SoundMeter;
import edu.bk.thesis.biodiary.core.voice.math.mfcc.FeatureVector;
import edu.bk.thesis.biodiary.core.voice.math.vq.Codebook;
import edu.bk.thesis.biodiary.utils.MessageHelper;
import edu.bk.thesis.biodiary.utils.SerializeArray;
import edu.bk.thesis.biodiary.utils.StorageHelper;


public class SetupVoiceFragment extends BaseVoiceFragment
{

    private static final String TAG = SetupVoiceFragment.class.getSimpleName();

    @BindView (R.id.setup_voice_pb_audio_quantity)
    ProgressBar mAudioQuantityProgressBar;

    private ComputeFeaturesTask mComputeFeaturesTask;
    private int                 mAudioQuantityCounter;

    private FeatureVector  mUserFeatureVector;
    private List<Codebook> mUserCodebook;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_voice, container, false);

        ButterKnife.bind(this, view);

        mSoundLevelDialog.setMessage("Say: \"My voice is my password, and it should log me in\"");

        init();

        return view;
    }

    @OnClick (R.id.setup_voice_pb_audio_quantity)
    void recordVoice()
    {
        if (mAudioQuantityCounter < Constants.VOICE_AUDIO_QUANTITY) {
            Log.i(TAG, "Recording...");
            MessageHelper.showToast(getActivity(),
                                    "Clearly read the phrase out loud.",
                                    Toast.LENGTH_LONG);
            startRecording();
        }
        else {
            trainVoices();
        }
    }

    void trainVoices()
    {
        if (mComputeFeaturesTask != null &&
            mComputeFeaturesTask.getStatus() != AsyncTask.Status.FINISHED) {
            MessageHelper.showToast(getActivity(),
                                    "Training task is still running",
                                    Toast.LENGTH_SHORT);
        }
        else {
            mProcessingDialog.show();
            new TrainTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void startRecording()
    {
        mProcessingDialog.show();
        mSoundLevelDialog.show();

        SoundMeter soundMeter = new SoundMeter();
        double     amplitude  = 0.0;
        try {
            soundMeter.start();
            MessageHelper.showToast(getActivity(), "Please keep silence!", Toast.LENGTH_LONG);
            Thread.sleep(3000);
            amplitude = soundMeter.getAmplitude();
            soundMeter.stop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mComputeFeaturesTask = new ComputeFeaturesTask(
            String.valueOf(mAudioQuantityCounter) + "_" + amplitude);
        mComputeFeaturesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void init()
    {
        mAudioQuantityCounter = 0;

        mAudioQuantityProgressBar.setProgress(0);
        mAudioQuantityProgressBar.setMax(Constants.VOICE_AUDIO_QUANTITY);
        mAudioQuantityProgressBar.setSecondaryProgress(Constants.VOICE_AUDIO_QUANTITY);
    }

    private class ComputeFeaturesTask extends AsyncTask<Void, Void, Boolean>
    {

        public ComputeFeaturesTask(String filename)
        {
            mVoiceAuthenticator.setOutputFile(filename);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Log.i(TAG, "Compute features of new audio");
            Log.d(TAG, "Mic threshold: " + mVoiceAuthenticator.getMicThreshold());
            mVoiceAuthenticator.startRecording();
            mSoundLevelDialog.dismiss();

            mUserFeatureVector = mVoiceAuthenticator.computeFeature(mUserFeatureVector);

            if (mUserFeatureVector == null) {
                Log.d(TAG, "Error with training voice, check if activeFile is set");
                return false;
            }
            else {
                mAudioQuantityCounter++;
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            mProcessingDialog.dismiss();

            if (result) {
                mAudioQuantityProgressBar.setProgress(mAudioQuantityCounter);
                MessageHelper.showToast(getActivity(), "Feature extracted!", Toast.LENGTH_LONG);
            }
            else {
                MessageHelper.showToast(getActivity(), "Error Training voice", Toast.LENGTH_LONG);
            }

            Log.i(TAG, "Done Training Voice");
        }
    }

    private class TrainTask extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return mVoiceAuthenticator.train(mUserFeatureVector);
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            mProcessingDialog.dismiss();
            if (result) {
                mUserCodebook = mVoiceAuthenticator.getCodeBook();
                SerializeArray.INSTANCE.saveArray(mUserCodebook,
                                                  StorageHelper.INSTANCE.retrievePrivatePath(
                                                      getActivity(),
                                                      "codebook.yml"));
                MessageHelper.showToast(getActivity(),
                                        "Saved user successfully",
                                        Toast.LENGTH_SHORT);
            }
            else {
                MessageHelper.showToast(getActivity(),
                                        "Error ocurred when finishing the training",
                                        Toast.LENGTH_LONG);
            }
        }
    }
}
