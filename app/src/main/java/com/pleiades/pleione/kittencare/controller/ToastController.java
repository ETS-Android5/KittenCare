package com.pleiades.pleione.kittencare.controller;

import android.content.Context;
import android.widget.Toast;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.object.Costume;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_CHOCO;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GIFT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SAILOR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_FREE;

public class ToastController {
    private final Context context;

    public ToastController(Context context) {
        this.context = context;
    }

    // TODO update costume
    public void showCostumeConditionToast(Costume costume) {
        if (costume.costumeType == COSTUME_TYPE_FREE) {
            int conditionLevel = Converter.getCostumeLevel(costume.costumeCode);
            String message = String.format(context.getString(R.string.costume_condition_level), conditionLevel);
            showToast(message, Toast.LENGTH_SHORT);
        } else if (costume.costumeCode == COSTUME_CODE_GIFT) {
            showToast(context.getString(R.string.costume_condition_gift), Toast.LENGTH_SHORT);
        } else if (costume.costumeCode == COSTUME_CODE_CHOCO) {
            showToast(context.getString(R.string.costume_condition_choco), Toast.LENGTH_SHORT);
        } else if (costume.costumeCode == COSTUME_CODE_GAME_MACHINE){
            showToast(context.getString(R.string.costume_condition_game_machine), Toast.LENGTH_SHORT);
        } else if (costume.costumeCode == COSTUME_CODE_SAILOR){
            showToast(context.getString(R.string.costume_condition_sailor), Toast.LENGTH_SHORT);
        }
    }

    public void showToast(String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}
