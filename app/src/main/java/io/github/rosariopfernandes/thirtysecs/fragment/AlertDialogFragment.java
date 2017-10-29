package io.github.rosariopfernandes.thirtysecs.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import io.github.rosariopfernandes.thirtysecs.R;

/**
 * Created by rosariopfernandes on 10/6/17.
 */

public class AlertDialogFragment extends android.support.v4.app.DialogFragment {

    private AlertDialog.Builder builder;

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.prompt_exit_title);
        builder.setMessage(R.string.prompt_exit_message);
        builder.setPositiveButton(R.string.action_cancel, null);
        builder.setNegativeButton(R.string.action_quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        return builder.create();
    }
}
