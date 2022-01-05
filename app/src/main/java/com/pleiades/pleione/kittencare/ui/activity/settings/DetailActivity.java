package com.pleiades.pleione.kittencare.ui.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.InputDialogFragment;

import java.util.ArrayList;

import static com.pleiades.pleione.kittencare.Config.DETAIL_POSITION_DRESS_RANDOM_COSTUME;
import static com.pleiades.pleione.kittencare.Config.DETAIL_POSITION_MODIFY_COORDINATES;
import static com.pleiades.pleione.kittencare.Config.DETAIL_POSITION_RECOGNIZE_KEYBOARD;
import static com.pleiades.pleione.kittencare.Config.DETAIL_POSITION_RENAME;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_RENAME;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_DRESS_RANDOMLY;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_RECOGNIZING_KEYBOARD;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class DetailActivity extends AppCompatActivity {
    private Context context;

    private ArrayList<DetailSetting> detailSettingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // initialize context
        context = DetailActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_detail);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize detail setting array list
        detailSettingArrayList = new ArrayList<>();
        detailSettingArrayList.add(DETAIL_POSITION_RENAME, new DetailSetting(getString(R.string.setting_title_rename_kitten), false));
        detailSettingArrayList.add(DETAIL_POSITION_MODIFY_COORDINATES, new DetailSetting(getString(R.string.setting_title_coordinates), false));
        detailSettingArrayList.add(DETAIL_POSITION_DRESS_RANDOM_COSTUME, new DetailSetting(getString(R.string.setting_title_dress_random_costume), true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            detailSettingArrayList.add(DETAIL_POSITION_RECOGNIZE_KEYBOARD, new DetailSetting(getString(R.string.setting_title_recognize_keyboard), true));

        // initialize detail settings recycler view
        RecyclerView detailSettingsRecyclerView = findViewById(R.id.recycler_detail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        detailSettingsRecyclerView.setLayoutManager(linearLayoutManager);
//        detailSettingsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // initialize detail settings recycler adapter
        DetailSettingsRecyclerAdapter detailSettingsRecyclerAdapter = new DetailSettingsRecyclerAdapter();
        detailSettingsRecyclerView.setAdapter(detailSettingsRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class DetailSetting {
        String title;
        boolean switchVisibility;

        DetailSetting(String title, boolean switchVisibility) {
            this.title = title;
            this.switchVisibility = switchVisibility;
        }
    }

    private class DetailSettingsRecyclerAdapter extends RecyclerView.Adapter<DetailSettingsRecyclerAdapter.DetailSettingsViewHolder> {
        private boolean isCheckLocked;

        private class DetailSettingsViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            SwitchCompat switchCompat;
            ImageView arrowImageView;

            public DetailSettingsViewHolder(@NonNull View itemView) {
                super(itemView);

                // initialize components
                titleTextView = itemView.findViewById(R.id.title_recycler_setting_switch);
                switchCompat = itemView.findViewById(R.id.switch_recycler_setting_switch);
                arrowImageView = itemView.findViewById(R.id.arrow_recycler_setting_switch);

                // set item view on click listener
                itemView.setOnClickListener(v -> {
                    // initialize position
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION)
                        return;

                    if (position == DETAIL_POSITION_RENAME) {
                        InputDialogFragment inputDialogFragment = new InputDialogFragment(DIALOG_TYPE_RENAME);
                        inputDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_RENAME));
                    } else if (position == DETAIL_POSITION_MODIFY_COORDINATES) {
                        new ToastController(context).showToast(getString(R.string.toast_todo), Toast.LENGTH_SHORT);
                    }
                });
            }
        }

        @NonNull
        @Override
        public DetailSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_setting_switch, parent, false);
            return new DetailSettingsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailSettingsViewHolder holder, int position) {
            final SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefs.edit();
            DetailSetting detailSetting = detailSettingArrayList.get(position);

            // set title
            holder.titleTextView.setText(detailSetting.title);

            // set switch, arrow visibility
            if (detailSetting.switchVisibility) {
                holder.switchCompat.setVisibility(View.VISIBLE);
                holder.arrowImageView.setVisibility(View.GONE);
            } else {
                holder.switchCompat.setVisibility(View.GONE);
                holder.arrowImageView.setVisibility(View.VISIBLE);
            }

            // set switch check change listener
            holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isCheckLocked)
                    return;

                if (position == DETAIL_POSITION_RECOGNIZE_KEYBOARD) {
                    editor.putBoolean(KEY_IS_RECOGNIZING_KEYBOARD, isChecked);
                    editor.apply();

                    // restart kitten service
                    if (KittenService.kittenView != null) {
                        context.stopService(new Intent(context, KittenService.class));
                        context.startService(new Intent(context, KittenService.class));
                    }
                } else if (position == DETAIL_POSITION_DRESS_RANDOM_COSTUME) {
                    editor.putBoolean(KEY_IS_DRESS_RANDOMLY, isChecked);
                    editor.apply();
                }
            });

            // set checked
            isCheckLocked = true;
            if (position == DETAIL_POSITION_RECOGNIZE_KEYBOARD)
                holder.switchCompat.setChecked(prefs.getBoolean(KEY_IS_RECOGNIZING_KEYBOARD, false));
            else if (position == DETAIL_POSITION_DRESS_RANDOM_COSTUME)
                holder.switchCompat.setChecked(prefs.getBoolean(KEY_IS_DRESS_RANDOMLY, false));
            isCheckLocked = false;
        }

        @Override
        public int getItemCount() {
            return detailSettingArrayList.size();
        }
    }

}