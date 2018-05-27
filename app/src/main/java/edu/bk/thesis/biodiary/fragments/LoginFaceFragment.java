package edu.bk.thesis.biodiary.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bytedeco.javacpp.opencv_core.Mat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.face.CvCameraPreview;
import edu.bk.thesis.biodiary.core.face.Face;
import edu.bk.thesis.biodiary.core.face.FaceDetection;
import edu.bk.thesis.biodiary.core.face.FaceVerification;
import edu.bk.thesis.biodiary.core.face.JavaCvUtils;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;
import edu.bk.thesis.biodiary.utils.MessageHelper;
import edu.bk.thesis.biodiary.utils.PermissionHelper;


public class LoginFaceFragment extends Fragment implements CvCameraPreview.CvCameraViewListener
{

    private static final String TAG = LoginFaceFragment.class.getSimpleName();

    @BindView (R.id.login_face_camera_view)
    CvCameraPreview mCameraView;

    private FaceDetection    mFaceDetector;
    private FaceVerification mFaceVerifior;

    private Face mFaceInFrame;

    private FaceVerification.PredictTask.Callback mPredictFaceTaskCallback
        = new FaceVerification.PredictTask.Callback()
    {
        @Override
        public void onPredictComplete(double distance)
        {
            MessageHelper.showToast(getActivity(), "Distance: " + distance, Toast.LENGTH_LONG);
        }
    };
    private PreferencesHandler mPreferencesHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login_face, container, false);
        ButterKnife.bind(this, view);

        PermissionHelper.requestPermissions(getActivity(), 2, Manifest.permission.CAMERA);

        mCameraView.setCvCameraViewListener(this);

        mFaceDetector = new FaceDetection(getActivity());
        mFaceVerifior = new FaceVerification();
        mFaceVerifior.loadTrainedModel();

        mPreferencesHandler = new PreferencesHandler(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {

    }

    @Override
    public void onCameraViewStopped()
    {

    }

    @Override
    public Mat onCameraFrame(Mat image)
    {
        mFaceInFrame = mFaceDetector.detect(image, "test_" + System.currentTimeMillis());
        if (mFaceInFrame != null) {
            JavaCvUtils.INSTANCE.showDetectedFace(mFaceInFrame, image);
        }
        return image;
    }

    @OnClick (R.id.login_face_btn_take_picture)
    void takePicture()
    {
        if (mFaceInFrame != null) {
            mCameraView.shootSound();
            Log.d(TAG, "Take picture and start verification...");

            Log.d(TAG,
                  "Size: " + mFaceInFrame.getAlignedFaceImage().rows() + " x " +
                  mFaceInFrame.getAlignedFaceImage().cols());

            mFaceInFrame.save();
            mFaceVerifior.predict(mFaceInFrame, mPredictFaceTaskCallback);

            mFaceInFrame = null;
        }
    }
}
