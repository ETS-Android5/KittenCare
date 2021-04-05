package com.pleiades.pleione.kittencare.ui.fragment.tutorial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AnimationController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.DELAY_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_OVERLAY;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_FIRST_EXPLORE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_EXPLORED;
import static com.pleiades.pleione.kittencare.Config.KEY_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class FirstTutorialFragment extends Fragment {
    private Context context;

    private long showTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_first, container, false);

        // initialize show button
        Button showButton = rootView.findViewById(R.id.button_tutorial_first);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

                // start kitten service
                if (KittenService.kittenView == null) {
                    // check overlay permission
                    if (Settings.canDrawOverlays(context)) {
                        // start kitten service
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            context.startForegroundService(new Intent(context, KittenService.class));
                        else
                            context.startService(new Intent(context, KittenService.class));

                        // case first exploring
                        if (!prefs.getBoolean(KEY_IS_EXPLORED, false)) {
                            PrefsController prefsController = new PrefsController(context);
                            prefsController.addItemPrefs(ITEM_CODE_MINT_ICE_CREAM, 1);
                            prefsController.addItemPrefs(ITEM_CODE_MINT_CAKE, 1);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(KEY_IS_EXPLORED, true);
                            editor.apply();

                            // add history
                            new PrefsController(context).addHistoryPrefs(HISTORY_TYPE_FIRST_EXPLORE, 0);
                        }

                        showTime = SystemClock.elapsedRealtime();
                    } else {
                        // show overlay dialog
                        DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_OVERLAY);
                        defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_OVERLAY));
                    }
                }
                // stop kitten service
                else {
                    // initialize show position
                    int showPosition = prefs.getInt(KEY_SHOW_POSITION, DEFAULT_SHOW_POSITION);

                    // case double click
                    if (SystemClock.elapsedRealtime() - showTime < (AnimationController.calculateDurationGravity(context, showPosition, true) + DELAY_DEFAULT))
                        return;

                    // stop kitten service
                    context.stopService(new Intent(context, KittenService.class));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
