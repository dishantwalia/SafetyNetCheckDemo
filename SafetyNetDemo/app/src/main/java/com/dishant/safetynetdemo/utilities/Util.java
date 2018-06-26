package com.dishant.safetynetdemo.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by punchh_dishant on 19,June,2018
 */
public class Util {

    public static void showAlert(Context context, String title, String text, String positive,
                                 DialogInterface.OnClickListener positiveClick,
                                 Dialog.OnKeyListener onBackPressed) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.setCancelable(false);
        alertBuilder.setOnKeyListener(onBackPressed);
        alertBuilder.setPositiveButton(positive, positiveClick);
        alertBuilder.create().show();
    }


}

