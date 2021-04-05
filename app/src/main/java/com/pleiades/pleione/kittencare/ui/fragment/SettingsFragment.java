package com.pleiades.pleione.kittencare.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.ui.activity.settings.ApplicationActivity;
import com.pleiades.pleione.kittencare.ui.activity.settings.DetailActivity;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_MAGNET_PERCENTAGE_HEIGHT;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_MAGNET_PERCENTAGE_WIDTH;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.KEY_ANIMATOR_DURATION_SCALE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.KEY_MAGNET_PERCENTAGE_HEIGHT;
import static com.pleiades.pleione.kittencare.Config.KEY_MAGNET_PERCENTAGE_WIDTH;
import static com.pleiades.pleione.kittencare.Config.KEY_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.POSITION_APPLICATION;
import static com.pleiades.pleione.kittencare.Config.POSITION_DETAIL;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.PROGRESS_TERM_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.PROGRESS_TERM_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.PROGRESS_TERM_MAGNET_PERCENTAGE;
import static com.pleiades.pleione.kittencare.Config.PROGRESS_TERM_SCALE_PERCENTAGE;
import static com.pleiades.pleione.kittencare.Config.PROGRESS_TERM_SHOW_POSITION;

public class SettingsFragment extends Fragment {
    private Context context;
    private View rootView;

    private ArrayList<String> generalSettingArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // initialize kitten configs
        initializeKittenConfigs();

        // initialize kitten values
        initializeKittenValues();

        // initialize general settings
        initializeGeneralSettings();

        return rootView;
    }

    private void initializeKittenConfigs() {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        SeekBar seekBar;

        // initialize configs
        float magnetWidthPercentage = prefs.getFloat(KEY_MAGNET_PERCENTAGE_WIDTH, DEFAULT_MAGNET_PERCENTAGE_WIDTH);
        float magnetHeightPercentage = prefs.getFloat(KEY_MAGNET_PERCENTAGE_HEIGHT, DEFAULT_MAGNET_PERCENTAGE_HEIGHT);
        int jumpAltitude = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        int jumpDistance = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        int showPosition = prefs.getInt(KEY_SHOW_POSITION, DEFAULT_SHOW_POSITION);
        float animatorDurationScale = prefs.getFloat(KEY_ANIMATOR_DURATION_SCALE, 1);

        // initialize seek bar 1
        seekBar = rootView.findViewById(R.id.seek_setting_1);
        seekBar.setMax(4); // 0 1 2 3 4
        for (int i = 0; i <= 4; i++)
            if (magnetWidthPercentage == ((i - 2) * PROGRESS_TERM_MAGNET_PERCENTAGE + DEFAULT_MAGNET_PERCENTAGE_WIDTH))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putFloat(KEY_MAGNET_PERCENTAGE_WIDTH, (progress - 2) * PROGRESS_TERM_MAGNET_PERCENTAGE + DEFAULT_MAGNET_PERCENTAGE_WIDTH);
                editor.apply();

                initializeKittenValues();
            }
        });

        // initialize seek bar 2
        seekBar = rootView.findViewById(R.id.seek_setting_2);
        seekBar.setMax(4); // 0 1 2 3 4
        for (int i = 0; i <= 4; i++)
            if (magnetHeightPercentage == ((i - 2) * PROGRESS_TERM_MAGNET_PERCENTAGE + DEFAULT_MAGNET_PERCENTAGE_HEIGHT))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putFloat(KEY_MAGNET_PERCENTAGE_HEIGHT, (progress - 2) * PROGRESS_TERM_MAGNET_PERCENTAGE + DEFAULT_MAGNET_PERCENTAGE_HEIGHT);
                editor.apply();

                initializeKittenValues();
            }
        });

        // initialize seek bar 3
        seekBar = rootView.findViewById(R.id.seek_setting_3);
        seekBar.setMax(4);
        for (int i = 0; i <= 4; i++)
            if (jumpAltitude == ((i - 2) * PROGRESS_TERM_JUMP_ALTITUDE + DEFAULT_JUMP_ALTITUDE))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt(KEY_JUMP_ALTITUDE, (progress - 2) * PROGRESS_TERM_JUMP_ALTITUDE + DEFAULT_JUMP_ALTITUDE);
                editor.apply();

                initializeKittenValues();
            }
        });

        // initialize seek bar 4
        seekBar = rootView.findViewById(R.id.seek_setting_4);
        seekBar.setMax(4);
        for (int i = 0; i <= 4; i++)
            if (jumpDistance == ((i - 2) * PROGRESS_TERM_JUMP_DISTANCE + DEFAULT_JUMP_DISTANCE))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt(KEY_JUMP_DISTANCE, (progress - 2) * PROGRESS_TERM_JUMP_DISTANCE + DEFAULT_JUMP_DISTANCE);
                editor.apply();

                initializeKittenValues();
            }
        });

        // initialize seek bar 5
        seekBar = rootView.findViewById(R.id.seek_setting_5);
        seekBar.setMax(4);
        for (int i = 0; i <= 4; i++)
            if (showPosition == ((i - 2) * PROGRESS_TERM_SHOW_POSITION + DEFAULT_SHOW_POSITION))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt(KEY_SHOW_POSITION, (progress - 2) * PROGRESS_TERM_SHOW_POSITION + DEFAULT_SHOW_POSITION);
                editor.apply();

                initializeKittenValues();
            }
        });

        // initialize seek bar 6
        seekBar = rootView.findViewById(R.id.seek_setting_6);
        seekBar.setMax(4);
        for (int i = 0; i <= 4; i++)
            if (animatorDurationScale == ((i - 2) * PROGRESS_TERM_SCALE_PERCENTAGE + 1))
                seekBar.setProgress(i);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putFloat(KEY_ANIMATOR_DURATION_SCALE, (progress - 2) * PROGRESS_TERM_SCALE_PERCENTAGE + 1);
                editor.apply();

                initializeKittenValues();
            }
        });
    }

    private void initializeKittenValues() {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        TextView seekValueTextView;

        // initialize configs
        float magnetWidthPercentage = prefs.getFloat(KEY_MAGNET_PERCENTAGE_WIDTH, DEFAULT_MAGNET_PERCENTAGE_WIDTH);
        float magnetHeightPercentage = prefs.getFloat(KEY_MAGNET_PERCENTAGE_HEIGHT, DEFAULT_MAGNET_PERCENTAGE_HEIGHT);
        int jumpAltitude = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        int jumpDistance = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        int showPosition = prefs.getInt(KEY_SHOW_POSITION, DEFAULT_SHOW_POSITION);
        float animatorDurationScale = prefs.getFloat(KEY_ANIMATOR_DURATION_SCALE, 1);

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_1);
        seekValueTextView.setText(String.format(Locale.getDefault(), "%d %%", Math.round(magnetWidthPercentage * 100)));

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_2);
        seekValueTextView.setText(String.format(Locale.getDefault(), "%d %%", Math.round(magnetHeightPercentage * 100)));

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_3);
        seekValueTextView.setText(String.valueOf(jumpAltitude));

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_4);
        seekValueTextView.setText(String.valueOf(jumpDistance));

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_5);
        seekValueTextView.setText(String.valueOf(showPosition));

        seekValueTextView = rootView.findViewById(R.id.seek_value_settings_6);
        seekValueTextView.setText(String.format(Locale.getDefault(), "%.1f x", animatorDurationScale));
    }

    private void initializeGeneralSettings() {
        // initialize general settings array list
        generalSettingArrayList = new ArrayList<>();
        generalSettingArrayList.add(POSITION_APPLICATION, getString(R.string.label_application));
        generalSettingArrayList.add(POSITION_DETAIL, getString(R.string.label_detail));

        // initialize general settings recycler view
        RecyclerView generalSettingsRecyclerView = rootView.findViewById(R.id.recycler_settings);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        generalSettingsRecyclerView.setLayoutManager(linearLayoutManager);
//        generalSettingsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // initialize general settings recycler adapter
        GeneralSettingsRecyclerAdapter generalSettingsRecyclerAdapter = new GeneralSettingsRecyclerAdapter();
        generalSettingsRecyclerView.setAdapter(generalSettingsRecyclerAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    private class GeneralSettingsRecyclerAdapter extends RecyclerView.Adapter<GeneralSettingsRecyclerAdapter.GeneralSettingsViewHolder> {

        private class GeneralSettingsViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;

            public GeneralSettingsViewHolder(@NonNull View itemView) {
                super(itemView);

                // initialize title text view
                titleTextView = itemView.findViewById(R.id.title_recycler_setting_default);

                // set item view on click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // initialize position
                        int position = getAdapterPosition();
                        if (position == RecyclerView.NO_POSITION)
                            return;

                        if (position == POSITION_APPLICATION) {
                            Intent intent = new Intent(context, ApplicationActivity.class);
                            startActivity(intent);
                        } else if (position == POSITION_DETAIL) {
                            Intent intent = new Intent(context, DetailActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public GeneralSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_setting_default, parent, false);
            return new GeneralSettingsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GeneralSettingsViewHolder holder, int position) {
            holder.titleTextView.setText(generalSettingArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return generalSettingArrayList.size();
        }

    }

}