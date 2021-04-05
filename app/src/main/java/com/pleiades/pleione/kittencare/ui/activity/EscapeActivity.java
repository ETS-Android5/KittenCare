package com.pleiades.pleione.kittencare.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_ESCAPE;

public class EscapeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_ESCAPE);
        defaultDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_ESCAPE));
    }
}
