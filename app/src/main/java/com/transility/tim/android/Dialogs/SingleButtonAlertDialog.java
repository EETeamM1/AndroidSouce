package com.transility.tim.android.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.transility.tim.android.R;

/**
 * Dialog that shows the passed Alert along with the
 *
 * Created by ambesh.kukreja on 6/29/2016.
 */
public class SingleButtonAlertDialog extends DialogFragment {


    private static  String TITLE="title";
    public interface SingleButtonAlertDialogInterface{
        void onOkButtonClick();
    }

    private SingleButtonAlertDialogInterface singleButtonAlertDialogInterface;
    public static SingleButtonAlertDialog newInstance(String title) {

        SingleButtonAlertDialog frag = new SingleButtonAlertDialog();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    public void setSingleButtonAlertDialogInterface(SingleButtonAlertDialogInterface singleButtonAlertDialogInterface){
        this.singleButtonAlertDialogInterface=singleButtonAlertDialogInterface;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle=getArguments();
        return new AlertDialog.Builder(getActivity())
                .setTitle(bundle.getString(TITLE))
                .setPositiveButton(R.string.textOk,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if (singleButtonAlertDialogInterface!=null)
                                singleButtonAlertDialogInterface.onOkButtonClick();
                                dismiss();

                            }
                        }
                )
                .create();
    }
}
