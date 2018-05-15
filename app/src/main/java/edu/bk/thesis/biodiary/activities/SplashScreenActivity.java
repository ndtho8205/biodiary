package edu.bk.thesis.biodiary.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import edu.bk.thesis.biodiary.core.face.Detection;
import edu.bk.thesis.biodiary.core.face.Verification;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;
import edu.bk.thesis.biodiary.utils.PermissionHelper;
import edu.bk.thesis.biodiary.utils.StorageHelper;


public class SplashScreenActivity extends AppCompatActivity
{

    static final int SETUP_REQUEST = 1;
    static final int LOGIN_REQUEST = 1;

    PreferencesHandler mPreferencesHandler;

    String[] permissions = {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!PermissionHelper.hasPermissions(this, permissions)) {
            PermissionHelper.requestPermissions(this, SETUP_REQUEST, permissions);
        }
        else {
            init();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (PermissionHelper.hasPermissions(this, permissions)) {
            init();
        }
    }

    private void init()
    {
        mPreferencesHandler = new PreferencesHandler(getApplicationContext());

        StorageHelper.INSTANCE.setupStorageDir();
        Detection.INSTANCE.load(getApplicationContext());

        if (mPreferencesHandler.isSetUp()) {
            Verification.INSTANCE.load(getApplicationContext());
        }

        if (!mPreferencesHandler.isSetUp()) {
            Intent intentToStartWelcomeActivity = new Intent(getApplicationContext(),
                                                             SetupActivity.class);
            startActivity(intentToStartWelcomeActivity);
            finish();
            return;
        }

        if (!mPreferencesHandler.isLoggedIn()) {
            Intent intentToStartLoginActivity = new Intent(getApplicationContext(),
                                                           LoginActivity.class);
            startActivity(intentToStartLoginActivity);
            finish();
            return;
        }

        if (mPreferencesHandler.isSetUp() && mPreferencesHandler.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), BioDiaryMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
