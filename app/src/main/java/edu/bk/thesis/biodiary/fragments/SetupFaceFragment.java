package edu.bk.thesis.biodiary.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.core.face.CvCameraPreview;
import edu.bk.thesis.biodiary.core.face.Detection;
import edu.bk.thesis.biodiary.core.face.Face;
import edu.bk.thesis.biodiary.core.face.Preprocessing;
import edu.bk.thesis.biodiary.core.face.Verification;
import edu.bk.thesis.biodiary.utils.PermissionHelper;

import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_core.flip;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;


public class SetupFaceFragment extends Fragment implements CvCameraPreview.CvCameraViewListener
{

    private static final String TAG = SetupFaceFragment.class.getSimpleName();

    @BindView (R.id.setup_face_camera_view)
    CvCameraPreview mCameraView;
    @BindView (R.id.setup_face_pb_pictures_quantity)
    ProgressBar     mPictureQuantityProgressBar;

    private Face mFaceInFrame;
    private List<Face> mFaceList = new ArrayList<>();

//     private TrainFacesTask mTrainFacesTask;
//     private TrainFacesTask.Callback mTrainFacesTaskCallback = new TrainFacesTask.Callback()
//     {
//         @Override
//         public void onTrainFacesComplete(boolean result)
//         {
//             if (result) {
//                 showToast("Training complete", Toast.LENGTH_SHORT);
//             }
//             else {
//                 showToast("Training failed", Toast.LENGTH_LONG);
//             }
//         }
//     };
//
//     private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext())
//     {
//         @Override
//         public void onManagerConnected(int status)
//         {
//             switch (status) {
//                 case LoaderCallbackInterface.SUCCESS:
//                     NativeMethods.loadNativeLibraries();
//                     Log.i(TAG, "OpenCV loaded successfully");
//                     mCameraView.enableView();
//
//                     mUserImages
//                             = mPreferencesHandler.getListMat(PreferencesHandler.KEY_USER_IMAGES);
//                     Log.i(TAG, "Number of user images loaded: " + mUserImages.size());
//                     if (!mUserImages.isEmpty()) {
// //                        trainFaces();
//                         Log.i(TAG,
//                               "Loaded User Images height: " + mUserImages.get(0).height() +
//                               " Width: " +
//                               mUserImages.get(0).width() + " total: " + mUserImages.get(0).total());
//                     }
//
//                     break;
//                 default:
//                     super.onManagerConnected(status);
//                     break;
//             }
//         }
//     };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setup_face, container, false);
        ButterKnife.bind(this, view);

        PermissionHelper.requestPermissions(getActivity(), 1, Manifest.permission.CAMERA);

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
    public Mat onCameraFrame(opencv_core.Mat mat)
    {
        Mat image     = mat;
        Mat grayImage = new Mat();

        // Flip image to get mirror effect
        if (mCameraView.isEmulator()) // Treat emulators as a special case
        {
            flip(image, image, 1);
        }
        cvtColor(image, grayImage, COLOR_BGR2GRAY);

        Face face = Detection.INSTANCE.detect(grayImage, String.valueOf(mFaceList.size()));
        if (face != null) {
            mFaceInFrame = face;
            showDetectedFace(face, image);
        }

        return image;
    }

    private void showDetectedFace(Face face, Mat image)
    {
        int x = face.getBoundingBox().getX();
        int y = face.getBoundingBox().getY();
        int w = face.getBoundingBox().getW();
        int h = face.getBoundingBox().getH();

        rectangle(image,
                  new Point(x, y),
                  new opencv_core.Point(x + w, y + h),
                  opencv_core.Scalar.YELLOW,
                  2,
                  LINE_8,
                  0);
    }

    private boolean trainFaces()
    {
        //     if (mUserImages.isEmpty()) {
        //         return true;
        //     }
        //
        //     if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
        //         Log.i(TAG, "mTrainFacesTask is still running");
        //         return false;
        //     }
        //
        //     Mat imagesMatrix = new Mat((int) mUserImages.get(0).total(),
        //                                mUserImages.size(),
        //                                mUserImages.get(0).type());
        //     for (int i = 0; i < mUserImages.size(); i++) {
        //         mUserImages.get(i)
        //                    .copyTo(imagesMatrix.col(i)); // Create matrix where each image is represented as a column vector
        //     }
        //     Log.i(TAG,
        //           "Images height: " + imagesMatrix.height() + " Width: " + imagesMatrix.width() +
        //           " total: " + imagesMatrix.total());
        //
        //     Log.i(TAG, "Training Eigenfaces");
        //     mTrainFacesTask = new TrainFacesTask(imagesMatrix, mTrainFacesTaskCallback);
        //     mTrainFacesTask.execute();

        return true;
    }

    @OnClick (R.id.setup_face_pb_pictures_quantity)
    void takePicture()
    {
        mCameraView.shootSound();

        if (mFaceInFrame != null) {
            Log.i(TAG, "Take picture for training later." + mFaceList.size());

            Preprocessing.INSTANCE.scaleToStandardSize(mFaceInFrame);

            mFaceList.add(mFaceInFrame);
            mPictureQuantityProgressBar.setProgress(mFaceList.size());
            if (mFaceList.size() == Verification.FACE_IMAGE_QUANTITY) {
                trainFaces();
            }
        }
    }
}
