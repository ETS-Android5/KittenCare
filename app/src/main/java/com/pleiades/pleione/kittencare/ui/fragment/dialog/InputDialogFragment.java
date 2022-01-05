package com.pleiades.pleione.kittencare.ui.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.DeviceController;

import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_RENAME;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SAILOR;
import static com.pleiades.pleione.kittencare.Config.KEY_NAME;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class InputDialogFragment extends androidx.fragment.app.DialogFragment {
    private Context context;
    private final int type;

    private EditText inputEditText;

    public InputDialogFragment(int type) {
        this.type = type;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // initialize activity
        Activity activity = getActivity();

        if (activity != null) {
            // initialize dialog view
            View dialogView = activity.getLayoutInflater().inflate(R.layout.fragment_dialog_input, null);

            // initialize and set message
            TextView messageTextView = dialogView.findViewById(R.id.message_dialog_input);
            if (type == DIALOG_TYPE_RENAME)
                messageTextView.setText(R.string.dialog_message_rename);

            // set positive listener
            dialogView.findViewById(R.id.positive_dialog_input).setOnClickListener(new onPositiveClickListener());

            // set negative listener
            dialogView.findViewById(R.id.negative_dialog_input).setOnClickListener(new onNegativeClickListener());

            // set negative visibility
            TextView negativeTextView = dialogView.findViewById(R.id.negative_dialog_input);
            switch (type) {
                default:
                    negativeTextView.setVisibility(View.VISIBLE);
                    break;
            }

            // set dialog view
            builder.setView(dialogView);
        }

        // create dialog
        Dialog dialog = builder.create();

        // set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set cancelable
        switch (type) {
            default:
                dialog.setCanceledOnTouchOutside(true);
                break;
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        int width = (int) ((new DeviceController(context)).getWidthMax() * 0.85);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);

            // request focus
            inputEditText = dialog.findViewById(R.id.edit_dialog_input);
            inputEditText.setOnFocusChangeListener((v, hasFocus) -> inputEditText.post(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT);
            }));
            inputEditText.requestFocus();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }

    private class onPositiveClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (type == DIALOG_TYPE_RENAME) {
                String kittenName = inputEditText.getText().toString();
                editor.putString(KEY_NAME, kittenName);
                editor.putBoolean(KEY_COSTUME_SAILOR, true);
                editor.apply();

                // restart kitten service
                if (KittenService.kittenView != null) {
                    context.stopService(new Intent(context, KittenService.class));
                    context.startService(new Intent(context, KittenService.class));
                }
            }

            dismiss();
        }
    }

    private class onNegativeClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            dismiss();
        }
    }
}
