package com.pleiades.pleione.kittencare.ui.activity.settings;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.ToastController;

import java.util.ArrayList;

import static com.pleiades.pleione.kittencare.Config.APPLICATION_POSITION_BACKUP;
import static com.pleiades.pleione.kittencare.Config.APPLICATION_POSITION_CHECK_UPDATE;
import static com.pleiades.pleione.kittencare.Config.APPLICATION_POSITION_OTHER_APPS;
import static com.pleiades.pleione.kittencare.Config.APPLICATION_POSITION_SHARE;
import static com.pleiades.pleione.kittencare.Config.APPLICATION_POSITION_TUTORIAL;
import static com.pleiades.pleione.kittencare.Config.URL_CLUSTER_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.URL_KITTEN;
import static com.pleiades.pleione.kittencare.Config.TOAST_POSITION_DEFAULT;

public class ApplicationActivity extends AppCompatActivity {
    private Context context;

    private ArrayList<String> applicationSettingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        // initialize context
        context = ApplicationActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_application);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize application setting array list
        applicationSettingArrayList = new ArrayList<>();
        applicationSettingArrayList.add(APPLICATION_POSITION_TUTORIAL, getString(R.string.label_tutorial));
        applicationSettingArrayList.add(APPLICATION_POSITION_SHARE, getString(R.string.setting_title_share_app));
        applicationSettingArrayList.add(APPLICATION_POSITION_CHECK_UPDATE, getString(R.string.setting_title_check_update));
        applicationSettingArrayList.add(APPLICATION_POSITION_BACKUP, getString(R.string.setting_title_request_backup));
        applicationSettingArrayList.add(APPLICATION_POSITION_OTHER_APPS, getString(R.string.setting_title_other_apps));

        // initialize application settings recycler view
        RecyclerView applicationSettingsRecyclerView = findViewById(R.id.recycler_application);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        applicationSettingsRecyclerView.setLayoutManager(linearLayoutManager);
//        applicationSettingsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // initialize application settings recycler adapter
        ApplicationSettingsRecyclerAdapter applicationSettingsRecyclerAdapter = new ApplicationSettingsRecyclerAdapter();
        applicationSettingsRecyclerView.setAdapter(applicationSettingsRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ApplicationSettingsRecyclerAdapter extends RecyclerView.Adapter<ApplicationSettingsRecyclerAdapter.ApplicationSettingsViewHolder> {

        private class ApplicationSettingsViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;

            public ApplicationSettingsViewHolder(@NonNull View itemView) {
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
                        if (position == APPLICATION_POSITION_TUTORIAL) {
                            Intent intent = new Intent(context, TutorialActivity.class);
                            startActivity(intent);
                        } else if (position == APPLICATION_POSITION_SHARE) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, URL_KITTEN);
                            Intent chooserIntent = Intent.createChooser(intent, null);
                            startActivity(chooserIntent);
                        } else if (position == APPLICATION_POSITION_CHECK_UPDATE) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(URL_KITTEN));
                            startActivity(intent);
                        } else if (position == APPLICATION_POSITION_BACKUP) {
                            BackupManager backupManager = new BackupManager(context);
                            backupManager.dataChanged();
                            new ToastController(context).showCustomToast(getString(R.string.toast_backup), Toast.LENGTH_SHORT, TOAST_POSITION_DEFAULT);
                        } else if (position == APPLICATION_POSITION_OTHER_APPS) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(URL_CLUSTER_PLEIADES));
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public ApplicationSettingsRecyclerAdapter.ApplicationSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_setting_default, parent, false);
            return new ApplicationSettingsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ApplicationSettingsRecyclerAdapter.ApplicationSettingsViewHolder holder, int position) {
            holder.titleTextView.setText(applicationSettingArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return applicationSettingArrayList.size();
        }
    }

}
