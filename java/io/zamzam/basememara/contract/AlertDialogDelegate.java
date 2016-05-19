package io.zamzam.basememara.contract;

import android.content.DialogInterface;

/**
 * Created by basem on 7/6/15.
 */
public interface AlertDialogDelegate {

    public String getNeutralText();
    public boolean onNeutralClick(DialogInterface dialog, int id);

}
