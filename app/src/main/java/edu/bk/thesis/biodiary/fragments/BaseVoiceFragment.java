package edu.bk.thesis.biodiary.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import edu.bk.thesis.biodiary.core.voice.VoiceAuthenticator;


public class BaseVoiceFragment extends Fragment
{

    private static final String TAG = BaseVoiceFragment.class.getSimpleName();

    protected ProgressDialog mSoundLevelDialog;
    protected ProgressDialog mProcessingDialog;

    protected VoiceAuthenticator mVoiceAuthenticator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSoundLevelDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        mSoundLevelDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSoundLevelDialog.setTitle("Listening...");
        mSoundLevelDialog.setCancelable(false);

        mProcessingDialog = new ProgressDialog(getActivity());
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
}
