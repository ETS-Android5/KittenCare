package com.pleiades.pleione.kittencare.object;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import static com.pleiades.pleione.kittencare.Config.DELAY_ANIMATION;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR_SLOW;

public class Scenario {
    private final ArrayList<String> scriptArrayList;
    private final ArrayList<String> translatedScriptArrayList;
    private final ArrayList<Long> durationArrayList, slowDurationArrayList;

    public Scenario(Context context, int scriptStringArrayResourceId, int translatedScriptStringArrayResourceId) {
        // initialize script array list
        this.scriptArrayList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(scriptStringArrayResourceId)));
        this.translatedScriptArrayList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(translatedScriptStringArrayResourceId)));

        // initialize duration array list
        this.durationArrayList = new ArrayList<>();
        this.slowDurationArrayList = new ArrayList<>();
        for (int i = 0; i < scriptArrayList.size(); i++) {
            long duration = DELAY_GAME_READ_CHAR * Math.max(scriptArrayList.get(i).length(), translatedScriptArrayList.get(i).length());
            durationArrayList.add(duration);

            long slowDuration = DELAY_GAME_READ_CHAR_SLOW * Math.max(scriptArrayList.get(i).length(), translatedScriptArrayList.get(i).length());
            slowDurationArrayList.add(slowDuration);
        }
    }

    public int getSize() {
        return scriptArrayList.size();
    }

    public String getScript(int index) {
        return scriptArrayList.get(index);
    }

    public String getTranslatedScript(int index) {
        return translatedScriptArrayList.get(index);
    }

    public long getDuration(int index) {
        return durationArrayList.get(index) + DELAY_ANIMATION;
    }

    public long getSlowDuration(int index) {
        return slowDurationArrayList.get(index) + DELAY_ANIMATION;
    }

    public String removeFirstScript() {
        return scriptArrayList.remove(0);
    }

    public String removeFirstTranslatedScript() {
        return translatedScriptArrayList.remove(0);
    }

    public long removeFirstDuration() {
        return durationArrayList.remove(0) + DELAY_ANIMATION;
    }
}
