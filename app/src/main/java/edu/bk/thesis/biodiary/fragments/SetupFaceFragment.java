package edu.bk.thesis.biodiary.fragments;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import edu.bk.thesis.biodiary.core.face.TinyDB;


public class SetupFaceFragment extends Fragment
        implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static final String TAG                      = SetupFaceFragment.class.getSimpleName();
    private static final int    PERMISSIONS_REQUEST_CODE = 0;
    private static final String LABEL                    = "user";

    private ArrayList<Mat>       images;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat                  mRgba, mGray;
    private Toast mToast;
    private float faceThreshold, distanceThreshold;
    private int               maximumImages;
    private SharedPreferences prefs;
    private TinyDB            tinydb;

    private TrainFacesTask mTrainFacesTask;
    private TrainFacesTask.Callback trainFacesTaskCallback = new TrainFacesTask.Callback()
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
                    NativeMethods.loadNativeLibraries(); // Load native libraries after(!) OpenCV initialization
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();

                    // Read images and labels from shared preferences
                    images = tinydb.getListMat("images");

                    Log.i(TAG,
                          "Number of images: " + images.size());
                    if (!images.isEmpty()) {
                        trainFaces(); // Train images after they are loaded
                        Log.i(TAG,
                              "Images height: " + images.get(0).height() + " Width: " +
                              images.get(0).width() + " total: " + images.get(0).total());
                    }

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

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

    /**
     * Train faces using stored images.
     *
     * @return Returns false if the task is already running.
     */
    private boolean trainFaces()
    {
        if (images.isEmpty()) {
            return true; // The array might be empty if the method is changed in the OnClickListener
        }

        if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "mTrainFacesTask is still running");
            return false;
        }

        Mat imagesMatrix = new Mat((int) images.get(0).total(),
                                   images.size(),
                                   images.get(0).type());
        for (int i = 0; i < images.size(); i++) {
            images.get(i)
                  .copyTo(imagesMatrix.col(i)); // Create matrix where each image is represented as a column vector
        }

        Log.i(TAG,
              "Images height: " + imagesMatrix.height() + " Width: " + imagesMatrix.width() +
              " total: " + imagesMatrix.total());

        Log.i(TAG, "Training Eigenfaces");
        showToast("Training " + getResources().getString(R.string.eigenfaces),
                  Toast.LENGTH_SHORT);

        mTrainFacesTask = new TrainFacesTask(imagesMatrix, trainFacesTaskCallback);

        mTrainFacesTask.execute();

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_face, container, false);


        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Set radio button based on value stored in shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        tinydb = new TinyDB(getContext()); // Used to store ArrayLists in the shared preferences


        view.findViewById(R.id.setup_face_btn_take_picture)
            .setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (mTrainFacesTask != null &&
                        mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
                        Log.i(TAG, "mTrainFacesTask is still running");
                        showToast("Still training...", Toast.LENGTH_SHORT);
                        return;
                    }

                    Log.i(TAG,
                          "Gray height: " + mGray.height() + " Width: " + mGray.width() +
                          " total: " +
                          mGray.total());
                    if (mGray.total() == 0) {
                        return;
                    }
                    Size imageSize = new Size(200,
                                              200.0f / ((float) mGray.width() /
                                                        (float) mGray.height())); // Scale image in order to decrease computation time
                    Imgproc.resize(mGray, mGray, imageSize);
                    Log.i(TAG,
                          "Small gray height: " + mGray.height() + " Width: " + mGray.width() +
                          " total: " + mGray.total());
                    //SaveImage(mGray);

                    Mat image = mGray.reshape(0, (int) mGray.total()); // Create column vector
                    Log.i(TAG,
                          "Vector height: " + image.height() + " Width: " + image.width() +
                          " total: " +
                          image.total());
                    images.add(image); // Add current image to the array

                    if (images.size() > maximumImages) {
                        images.remove(0); // Remove first image
//                        imagesLabels.remove(0); // Remove first label
                        Log.i(TAG, "The number of images is limited to: " + images.size());
                    }

                    trainFaces();
                }
            });

        mOpenCvCameraView = view.findViewById(R.id.setup_face_camera_view);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
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
    public void onPause()
    {
        super.onPause();

        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Read threshold values
        float progress = prefs.getFloat("faceThreshold", -1);
        if (progress != -1) {
            faceThreshold = progress;
        }
        progress = prefs.getFloat("distanceThreshold", -1);
        if (progress != -1) {
            distanceThreshold = progress;
        }
        maximumImages = 25;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        // Store threshold values
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("faceThreshold", faceThreshold);
        editor.putFloat("distanceThreshold", distanceThreshold);
        editor.putInt("maximumImages", maximumImages);
        editor.putInt("mCameraIndex", mOpenCvCameraView.mCameraIndex);
        editor.apply();

        // Store ArrayLists containing the images and labels
        if (images != null) {
            tinydb.putListMat("images", images);
//            tinydb.putListString("imagesLabels", imagesLabels);
        }
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

    private void loadOpenCV()
    {
        if (!OpenCVLoader.initDebug(true)) {
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
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
        int orientation = mOpenCvCameraView.getScreenOrientation();
//        if (mOpenCvCameraView.isEmulator()) // Treat emulators as a special case
//        {
//            Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
//        }
//        else {
        switch (orientation) { // RGB image
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Core.flip(mRgbaTmp, mRgbaTmp, 0); // Flip along x-axis
                }
                else {
                    Core.flip(mRgbaTmp, mRgbaTmp, -1); // Flip along both axis
                }
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
                }
                break;
        }
        switch (orientation) { // Grayscale image
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Core.flip(mGrayTmp, mGrayTmp, -1); // Flip along both axis
                }
                else {
                    Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                }
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                }
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                }
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                }
                break;
        }
//        }

        mGray = mGrayTmp;
        mRgba = mRgbaTmp;

        return mRgba;
    }
}
