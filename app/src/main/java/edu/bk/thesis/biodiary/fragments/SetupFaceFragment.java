package edu.bk.thesis.biodiary.fragments;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.face.CameraBridgeViewBase;
import edu.bk.thesis.biodiary.core.face.NativeMethods;
import edu.bk.thesis.biodiary.core.face.NativeMethods.TrainFacesTask;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;


public class SetupFaceFragment extends Fragment
        implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static final String TAG                      = SetupFaceFragment.class.getSimpleName();
    private static final int    PERMISSIONS_REQUEST_CODE = 0;
    private static final int    MAXIMUM_IMAGES           = 10;

    private Button               mNextStepButton;
    private ImageButton          mTakePictureButton;
    private CameraBridgeViewBase mCameraView;

    private Toast              mToast;
    private PreferencesHandler mPreferencesHandler;

    private ArrayList<Mat> mUserImages;
    private Mat            mRgba, mGray;

    private TrainFacesTask mTrainFacesTask;
    private TrainFacesTask.Callback mTrainFacesTaskCallback = new TrainFacesTask.Callback()
    {
        @Override
        public void onTrainFacesComplete(boolean result)
        {
            if (result) {
                showToast("Training complete", Toast.LENGTH_SHORT);
            }
            else {
                showToast("Training failed", Toast.LENGTH_LONG);
            }
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext())
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    NativeMethods.loadNativeLibraries();
                    Log.i(TAG, "OpenCV loaded successfully");
                    mCameraView.enableView();

                    mUserImages
                            = mPreferencesHandler.getListMat(PreferencesHandler.KEY_USER_IMAGES);
                    Log.i(TAG, "Number of user images loaded: " + mUserImages.size());
                    if (!mUserImages.isEmpty()) {
//                        trainFaces();
                        Log.i(TAG,
                              "Loaded User Images height: " + mUserImages.get(0).height() +
                              " Width: " +
                              mUserImages.get(0).width() + " total: " + mUserImages.get(0).total());
                    }

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadOpenCV();
            }
            else {
                showToast("Permission required!", Toast.LENGTH_LONG);
                getActivity().finish();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_face, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        mNextStepButton = view.findViewById(R.id.setup_face_btn_next);
//        mNextStepButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                ((SetupActivity) getActivity()).setCurrentStep(1);
//            }
//        });

        mTakePictureButton = view.findViewById(R.id.setup_face_btn_take_picture);
        mTakePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "Take picture and start training");

                if (mTrainFacesTask != null &&
                    mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "mTrainFacesTask is still running");
                    showToast("Still training...", Toast.LENGTH_SHORT);
                    return;
                }

                Log.i(TAG, "Gray height: " + mGray.height() + " Width: " + mGray.width() +
                           " total: " + mGray.total());
                if (mGray.total() == 0) {
                    return;
                }
                Size imageSize = new Size(200, 200.0f / ((float) mGray.width() /
                                                         (float) mGray.height()));
                Imgproc.resize(mGray, mGray, imageSize);
                Log.i(TAG, "Small gray height: " + mGray.height() + " Width: " + mGray.width() +
                           " total: " + mGray.total());

                Mat image = mGray.reshape(0, (int) mGray.total()); // Create column vector
                Log.i(TAG, "Vector height: " + image.height() + " Width: " + image.width() +
                           " total: " + image.total());
                mUserImages.add(image); // Add current image to the array

                if (mUserImages.size() > MAXIMUM_IMAGES) {
                    mUserImages.remove(0);
                    Log.i(TAG, "The number of images is limited to: " + mUserImages.size());
                }

                trainFaces();
            }
        });

        mCameraView = view.findViewById(R.id.setup_face_camera_view);
        mCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);

        mPreferencesHandler = new PreferencesHandler(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Request permission if needed
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                                              Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                                              new String[]{ Manifest.permission.CAMERA },
                                              PERMISSIONS_REQUEST_CODE);
        }
        else {
            loadOpenCV();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Store ArrayLists containing the images and labels
        if (mUserImages != null) {
            mPreferencesHandler.putListMat(PreferencesHandler.KEY_USER_IMAGES, mUserImages);
            Log.i(TAG, "Saved user images");
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height)
    {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped()
    {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Mat mGrayTmp = inputFrame.gray();
        Mat mRgbaTmp = inputFrame.rgba();

        // Flip image to get mirror effect
        int orientation = mCameraView.getScreenOrientation();
        if (mCameraView.isEmulator()) // Treat emulators as a special case
        {
            Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
        }
        else {
            switch (orientation) { // RGB image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                        Core.flip(mRgbaTmp, mRgbaTmp, 0); // Flip along x-axis
                    }
                    else {
                        Core.flip(mRgbaTmp, mRgbaTmp, -1); // Flip along both axis
                    }
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                        Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
                    }
                    break;
            }
            switch (orientation) { // Grayscale image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                        Core.flip(mGrayTmp, mGrayTmp, -1); // Flip along both axis
                    }
                    else {
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    }
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                        Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    }
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    }
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    if (mCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    }
                    break;
            }
        }

        mGray = mGrayTmp;
        mRgba = mRgbaTmp;

        return mRgba;
    }

    private void showToast(String message, int duration)
    {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG) {
            throw new IllegalArgumentException();
        }
        if (mToast != null && mToast.getView().isShown()) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), message, duration);
        mToast.show();
    }

    private boolean trainFaces()
    {
        if (mUserImages.isEmpty()) {
            return true;
        }

        if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "mTrainFacesTask is still running");
            return false;
        }

        Mat imagesMatrix = new Mat((int) mUserImages.get(0).total(),
                                   mUserImages.size(),
                                   mUserImages.get(0).type());
        for (int i = 0; i < mUserImages.size(); i++) {
            mUserImages.get(i)
                       .copyTo(imagesMatrix.col(i)); // Create matrix where each image is represented as a column vector
        }
        Log.i(TAG,
              "Images height: " + imagesMatrix.height() + " Width: " + imagesMatrix.width() +
              " total: " + imagesMatrix.total());

        Log.i(TAG, "Training Eigenfaces");
        mTrainFacesTask = new TrainFacesTask(imagesMatrix, mTrainFacesTaskCallback);
        mTrainFacesTask.execute();

        return true;
    }

    private void loadOpenCV()
    {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG,
                  "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                                   getContext(),
                                   mLoaderCallback);
        }
        else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
