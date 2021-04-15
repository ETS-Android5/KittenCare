package com.pleiades.pleione.kittencare.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.object.Costume;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_CHOCO;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GIFT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SAILOR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_FREE;
import static com.pleiades.pleione.kittencare.Config.TOAST_POSITION_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.TOAST_POSITION_HIGH;
import static com.pleiades.pleione.kittencare.Config.TOAST_POSITION_VERY_HIGH;

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
            showCustomToast(message, Toast.LENGTH_SHORT, TOAST_POSITION_HIGH);
        } else if (costume.costumeCode == COSTUME_CODE_GIFT) {
            showCustomToast(context.getString(R.string.costume_condition_gift), Toast.LENGTH_SHORT, TOAST_POSITION_HIGH);
        } else if (costume.costumeCode == COSTUME_CODE_CHOCO) {
            showCustomToast(context.getString(R.string.costume_condition_choco), Toast.LENGTH_SHORT, TOAST_POSITION_HIGH);
        } else if (costume.costumeCode == COSTUME_CODE_GAME_MACHINE){
            showCustomToast(context.getString(R.string.costume_condition_game_machine), Toast.LENGTH_SHORT, TOAST_POSITION_HIGH);
        } else if (costume.costumeCode == COSTUME_CODE_SAILOR){
            showCustomToast(context.getString(R.string.costume_condition_sailor), Toast.LENGTH_SHORT, TOAST_POSITION_HIGH);
        }
    }

    public void showCustomToast(String message, int duration, int position) {
        Toast toast = Toast.makeText(context, message, duration);

        // set background color
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            View view = toast.getView();
            view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.color_toast_background), PorterDuff.Mode.SRC_ATOP);

            // initialize message
            TextView messageTextView = view.findViewById(android.R.id.message);
            messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            messageTextView.setTextColor(Color.WHITE);
        }

        // set position
        if (position != TOAST_POSITION_DEFAULT) {
            // initialize resources
            Resources resources = context.getResources();

            // initialize layout offset
            int navHeight = resources.getDimensionPixelSize(R.dimen.height_navigation);
            int margin = resources.getDimensionPixelSize(R.dimen.margin_default);
            int textSize = resources.getDimensionPixelSize(R.dimen.text_size_default);
            int layoutOffset = navHeight + (2 * margin) + textSize;

            if (position == TOAST_POSITION_VERY_HIGH)
                layoutOffset += (2 * margin);

            // set toast offset
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, layoutOffset);

//            int toastOffsetId = Resources.getSystem().getIdentifier("toast_y_offset", "dimen", "android");
//            int defaultOffset = resources.getDimensionPixelSize(toastOffsetId);

//            int layoutOffset = 56 + 16 + 14;
//            if (position == TOAST_POSITION_VERY_HIGH)
//                layoutOffset = layoutOffset + 16;
//            layoutOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, layoutOffset, context.getResources().getDisplayMetrics()); // convert dp to pixel
        }

        toast.show();
    }
}
