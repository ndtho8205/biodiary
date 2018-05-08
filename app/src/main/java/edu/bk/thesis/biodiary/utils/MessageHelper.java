package edu.bk.thesis.biodiary.utils;

import android.content.Context;
import android.widget.Toast;


public class MessageHelper
{
    public static Toast mToast;

    private static void showToast(Context context, String message, int duration)
    {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG) {
            throw new IllegalArgumentException();
        }
        if (mToast != null && mToast.getView().isShown()) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }
}
