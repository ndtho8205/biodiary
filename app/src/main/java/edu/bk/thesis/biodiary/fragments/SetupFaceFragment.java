package edu.bk.thesis.biodiary.fragments;

import android.Manifest;
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
import edu.bk.thesis.biodiary.core.face.Detection;
import edu.bk.thesis.biodiary.core.face.Face;
import edu.bk.thesis.biodiary.core.face.JavaCvUtils;
import edu.bk.thesis.biodiary.core.face.Preprocessing;
import edu.bk.thesis.biodiary.core.face.Verification;
import edu.bk.thesis.biodiary.utils.MessageHelper;
import edu.bk.thesis.biodiary.utils.PermissionHelper;

import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;


public class SetupFaceFragment extends Fragment implements CvCameraPreview.CvCameraViewListener
{

    private static final String TAG = SetupFaceFragment.class.getSimpleName();

    @BindView (R.id.setup_face_camera_view)
    CvCameraPreview mCameraView;
    @BindView (R.id.setup_face_pb_pictures_quantity)
    ProgressBar     mPictureQuantityProgressBar;

    private Face mFaceInFrame;
    private List<Face> mFaceList = new ArrayList<>();

    private Verification.TrainTask mTrainTask;
    private Verification.TrainTask.Callback mTrainFacesTaskCallback
        = new Verification.TrainTask.Callback()
    {
        @Override
        public void onTrainComplete(boolean result)
        {
            if (result) {
                Log.d(TAG, "Save face list");
                for (Face face : mFaceList) {
                    face.save();
                }
                Log.d(TAG, "Save model");
                Verification.INSTANCE.save(getActivity());
                MessageHelper.showToast(getActivity(), "Training complete", Toast.LENGTH_SHORT);
            }
            else {
                MessageHelper.showToast(getActivity(),
                                        "Training failed. Take picture again.",
                                        Toast.LENGTH_LONG);
                mFaceList.clear();
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

        PermissionHelper.requestPermissions(getActivity(),
                                            1,
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE);

        mCameraView.setCvCameraViewListener(this);

        mPictureQuantityProgressBar.setProgress(0);
        mPictureQuantityProgressBar.setMax(Verification.FACE_IMAGE_QUANTITY);

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
        Mat grayImage = new Mat();

        cvtColor(image, grayImage, COLOR_BGR2GRAY);

        mFaceInFrame = Detection.INSTANCE.detect(grayImage, String.valueOf(mFaceList.size()));
        if (mFaceInFrame != null) {
            JavaCvUtils.INSTANCE.showDetectedFace(mFaceInFrame, image);
        }

        return image;
    }

    @OnClick (R.id.setup_face_pb_pictures_quantity)
    void takePicture()
    {
        if (mFaceInFrame != null && mFaceList.size() <= Verification.FACE_IMAGE_QUANTITY) {
            mCameraView.shootSound();
            Log.i(TAG, "Take picture for training later." + mFaceList.size());

            Preprocessing.INSTANCE.scaleToStandardSize(mFaceInFrame);
            System.out.println(mFaceInFrame.getAlignedImage().rows());
            System.out.println(mFaceInFrame.getAlignedImage().cols());

            mFaceList.add(mFaceInFrame);
            mPictureQuantityProgressBar.setProgress(mFaceList.size());
            if (mFaceList.size() == Verification.FACE_IMAGE_QUANTITY) {
                trainFaces();
            }

            mFaceInFrame = null;
        }
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
        mTrainTask = new Verification.TrainTask(getActivity(), mFaceList, mTrainFacesTaskCallback);
        mTrainTask.execute();

        return true;
    }
}
