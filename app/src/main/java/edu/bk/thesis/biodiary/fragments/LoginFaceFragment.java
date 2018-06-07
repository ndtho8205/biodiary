package edu.bk.thesis.biodiary.fragments;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.face.CvCameraPreview;
import edu.bk.thesis.biodiary.core.face.Face;
import edu.bk.thesis.biodiary.core.face.FaceDetection;
import edu.bk.thesis.biodiary.core.face.FaceQualityComputation;
import edu.bk.thesis.biodiary.core.face.FaceVerification;
import edu.bk.thesis.biodiary.core.face.JavaCvUtils;
import edu.bk.thesis.biodiary.models.FaceData;
import edu.bk.thesis.biodiary.utils.MessageHelper;
import edu.bk.thesis.biodiary.utils.PermissionHelper;


public class LoginFaceFragment extends Fragment implements CvCameraPreview.CvCameraViewListener
{

    private static final String TAG = LoginFaceFragment.class.getSimpleName();

    @BindView (R.id.login_face_camera_view)
    CvCameraPreview mCameraView;

    private FaceDetection          mFaceDetector;
    private FaceQualityComputation mQualityComputation;

    private Face mFaceInFrame;

    private FaceVerification.PredictTask mPredictTask;
    private OnLoginFaceCallbackReceived  mOnLoginFaceCallbackReceived;
    private FaceVerification.PredictTask.Callback mPredictFaceTaskCallback
        = new FaceVerification.PredictTask.Callback()
    {
        @Override
        public void onPredictComplete(boolean result, @NotNull Face face, double distance)
        {
            if (result) {

                double qualityBrightness
                    = mQualityComputation.computeBrightnessScore(face.getContainerImage());
                double qualityContrast
                    = mQualityComputation.computeContrastScore(face.getFaceImage());
                double qualitySharpness
                    = mQualityComputation.computeSharpnessScore(face.getFaceImage());

                String faceData = distance + ";" + qualityBrightness + ";" + qualityContrast + ";" +
                                  qualitySharpness;
                Log.d(TAG, faceData);


                face.setFaceImageName(face.getFaceImageName() + "_" + faceData);
                face.save();

                FaceData data = new FaceData(distance,
                                             qualityBrightness,
                                             qualityContrast,
                                             qualitySharpness);
//                FaceData data = new FaceData(distance / 8000.0,
//                                             qualityBrightness / 255.0,
//                                             (qualityContrast - 30.0) / 50.0,
//                                             (qualitySharpness - 1.0) / 10.0);

                mOnLoginFaceCallbackReceived.updateFaceData(data);

                MessageHelper.showToast(getActivity(),
                                        "Distance: " + distance + ". " +
                                        "Picture is taken. Next step, please.",
                                        Toast.LENGTH_LONG);
            }
            else {
                MessageHelper.showToast(getActivity(),
                                        "Computing feature failed. Take picture again.",
                                        Toast.LENGTH_LONG);
            }
        }
    };

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mOnLoginFaceCallbackReceived = (OnLoginFaceCallbackReceived) getActivity();
    }

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
        mQualityComputation = new FaceQualityComputation();

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
        if (!(mPredictTask != null && mPredictTask.getStatus() != AsyncTask.Status.FINISHED)) {
            mFaceInFrame = mFaceDetector.detect(image, "login_" + System.currentTimeMillis());
            if (mFaceInFrame != null) {
                JavaCvUtils.INSTANCE.showDetectedFace(mFaceInFrame, image);
            }
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

            predictFace();

            mFaceInFrame = null;
        }
    }

    private void predictFace()
    {
        if (mPredictTask != null && mPredictTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "Predicting task is still running");
        }
        else {

            mPredictTask = new FaceVerification.PredictTask(getActivity(),
                                                            mFaceInFrame,
                                                            mPredictFaceTaskCallback);
            mPredictTask.execute();
        }
    }

    public interface OnLoginFaceCallbackReceived
    {

        void updateFaceData(FaceData faceData);
    }
}
