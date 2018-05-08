package edu.bk.thesis.biodiary.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.voice.SoundFeature;


public class SetupVoiceFragment extends Fragment
{

    private Button       mRecordButton;
    private SoundFeature mSoundFeature;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_voice, container, false);

        // mSoundFeature = new SoundFeature();
        //
        // mRecordButton = view.findViewById(R.id.setup_voice_btn_record);
        // mRecordButton.setOnClickListener(new View.OnClickListener()
        // {
        //     @Override
        //     public void onClick(View v)
        //     {
        //         try {
        //             mSoundFeature.getOwner();
        //         }
        //         catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //     }
        // });

        return view;
    }
}
