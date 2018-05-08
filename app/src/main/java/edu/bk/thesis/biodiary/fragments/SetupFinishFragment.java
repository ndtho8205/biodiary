package edu.bk.thesis.biodiary.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.activities.SetupActivity;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class SetupFinishFragment extends Fragment
{

    Button mFinishButton;
    private PreferencesHandler mPreferencesHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_finish, container, false);

        mPreferencesHandler = new PreferencesHandler(getActivity().getApplicationContext());

        mFinishButton = view.findViewById(R.id.btn_setup_finish);
        mFinishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPreferencesHandler.setUp();
                ((SetupActivity) getActivity()).finishSetup();
            }
        });
        return view;
    }
}
