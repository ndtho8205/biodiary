package edu.bk.thesis.biodiary.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.activities.LoginActivity;
import edu.bk.thesis.biodiary.core.voice.SoundFeature;


public class LoginVoiceFragment extends Fragment
{

    TextView textView;
    private Button       mRecordButton;
    private Button       mDoneButton;
    private SoundFeature mSoundFeature;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login_voice, container, false);

        mDoneButton = view.findViewById(R.id.login_voice_btn_done);
        mDoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((LoginActivity) getActivity()).finishLogin();
            }
        });
        //
        // textView = view.findViewById(R.id.login_voice_textview);
        //
        // mSoundFeature = new SoundFeature();
        //
        // mRecordButton = view.findViewById(R.id.login_voice_btn_record);
        // mRecordButton.setOnClickListener(new View.OnClickListener()
        // {
        //     @Override
        //     public void onClick(View v)
        //     {
        //         try {
        //             mSoundFeature.getVoice();
        //             double voiceDistance = mSoundFeature.compare();
        //
        //             textView.setText(Double.toString(voiceDistance));
        //
        //         }
        //         catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //     }
        // });


        return view;
    }
}
