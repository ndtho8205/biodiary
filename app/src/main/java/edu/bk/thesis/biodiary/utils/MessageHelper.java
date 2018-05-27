package edu.bk.thesis.biodiary.utils;

import android.content.Context;
import android.widget.Toast;


public class MessageHelper
{

    private static Toast mToast;

    public static void showToast(Context context, String message, int duration)
    {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG) {
            throw new IllegalArgumentException();
        }
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }

    public static void cancelToast()
    {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
