package com.pleiades.pleione.kittencare;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import static com.pleiades.pleione.kittencare.Config.KEY_BACKUP;
import static com.pleiades.pleione.kittencare.Config.PREFS;

// usage
// BackupManager backupManager = new BackupManager(getContext());
// backupManager.dataChanged();

public class BackupAgent extends BackupAgentHelper {

    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(KEY_BACKUP, helper);
    }
}