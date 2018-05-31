package edu.bk.thesis.biodiary.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.face.CvCameraPreview;
import edu.bk.thesis.biodiary.core.face.Face;
import edu.bk.thesis.biodiary.core.face.FaceDetection;
import edu.bk.thesis.biodiary.core.face.FaceVerification;
import edu.bk.thesis.biodiary.core.face.JavaCvUtils;
import edu.bk.thesis.biodiary.utils.MessageHelper;


public class SetupFaceFragment extends Fragment implements CvCameraPreview.CvCameraViewListener
{

    private static final String TAG = SetupFaceFragment.class.getSimpleName();

    @BindView (R.id.setup_face_camera_view)
    CvCameraPreview mCameraView;
    @BindView (R.id.setup_face_pb_pictures_quantity)
    ProgressBar     mPictureQuantityProgressBar;

    private FaceDetection mFaceDetector;

    private Face       mFaceInFrame;
    private List<Face> mFaceList;

    private FaceVerification.TrainTask mTrainTask;
    private FaceVerification.TrainTask.Callback mTrainFacesTaskCallback
        = new FaceVerification.TrainTask.Callback()
    {
        @Override
        public void onTrainComplete(boolean result)
        {
            if (result) {
                Log.d(TAG, "Save face list");
                for (Face face : mFaceList) {
                    face.save();
                }
                MessageHelper.showToast(getActivity(), "Training complete", Toast.LENGTH_SHORT);
            }
            else {
                MessageHelper.showToast(getActivity(),
                                        "Training failed. Take picture again.",
                                        Toast.LENGTH_LONG);
                init();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_face, container, false);
        ButterKnife.bind(this, view);

        mCameraView.setCvCameraViewListener(this);

        mFaceDetector = new FaceDetection(getActivity());

        init();

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
        if (!(mTrainTask != null && mTrainTask.getStatus() != AsyncTask.Status.FINISHED)) {

            mFaceInFrame = mFaceDetector.detect(image, String.valueOf(mFaceList.size()));
            if (mFaceInFrame != null) {
                JavaCvUtils.INSTANCE.showDetectedFace(mFaceInFrame, image);
            }
        }

        return image;
    }

    @OnClick (R.id.setup_face_pb_pictures_quantity)
    void takePicture()
    {
        if (mFaceInFrame != null && mFaceList.size() < FaceVerification.FACE_IMAGE_QUANTITY) {
            mCameraView.shootSound();
            Log.i(TAG, "Take picture for training later." + mFaceList.size());

            Log.d(TAG,
                  mFaceInFrame.getAlignedFaceImage().rows() + "x" +
                  mFaceInFrame.getAlignedFaceImage().cols());

            mFaceList.add(mFaceInFrame);
            mPictureQuantityProgressBar.setProgress(mFaceList.size());
            if (mFaceList.size() == FaceVerification.FACE_IMAGE_QUANTITY) {
                trainFaces();
            }

            mFaceInFrame = null;
        }
    }

    private void init()
    {
        mFaceList = new ArrayList<>();

        mPictureQuantityProgressBar.setProgress(0);
        mPictureQuantityProgressBar.setMax(FaceVerification.FACE_IMAGE_QUANTITY);
    }

    private boolean trainFaces()
    {
        if (mFaceList.isEmpty()) {
            return true;
        }

        if (mTrainTask != null && mTrainTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "Training task is still running");
            return false;
        }

        Log.i(TAG, "Training Eigenfaces");
        mTrainTask = new FaceVerification.TrainTask(getActivity(),
                                                    mFaceList,
                                                    mTrainFacesTaskCallback);
        mTrainTask.execute();

        return true;
    }
}
