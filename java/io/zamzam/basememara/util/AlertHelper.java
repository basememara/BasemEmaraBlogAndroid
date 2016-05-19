package io.zamzam.basememara.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import io.zamzam.basememara.R;

/**
 * Created by basem on 7/7/15.
 */
public class AlertHelper {

    public static AlertDialog openMessage(final Context context, String message, final String title) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.show();

        return dialog;
    }

    public static ProgressDialog createSpinnerDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.LoadingSpinnerDialog);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        return progressDialog;
    }

}
