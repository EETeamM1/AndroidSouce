package com.transility.tim.android.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.transility.tim.android.R;

/**
 * Dialog that shows the passed Alert along with the
 * <p/>
 * Created by ambesh.kukreja on 6/29/2016.
 */
public class SingleButtonAlertDialog extends DialogFragment {


    private final static String TITLE = "title";

    public static SingleButtonAlertDialog newInstance(String title) {

        SingleButtonAlertDialog frag = new SingleButtonAlertDialog();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        return new AlertDialog.Builder(getActivity())
                .setTitle(bundle.getString(TITLE))
                .setPositiveButton(R.string.textOk,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                          dismiss();

                            }
                        }
                )
                .create();
    }

}
