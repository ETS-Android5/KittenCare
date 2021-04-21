package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_GREEN_TEA_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CONSUMPTION;

public class ExperienceTutorialFragment extends Fragment {
    private Context context;

    private ImageView arrowImageView;

    private Timer timer;
    private TimerTask timerTask;
    private boolean isHappy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_experience, container, false);

        int itemCode = ITEM_CODE_GREEN_TEA_ICE_CREAM;
        View itemView = rootView.findViewById(R.id.item_tutorial_experience);
        itemView.setBackground(null);
        ((TextView) itemView.findViewById(R.id.title_recycler_item)).setText(Converter.getItemName(context, itemCode));
        ((TextView) itemView.findViewById(R.id.contents_recycler_item)).setText(Converter.getItemEffect(context, itemCode));
        ((ImageView) itemView.findViewById(R.id.icon_recycler_item)).setImageResource(Converter.getItemIconResourceId(ITEM_TYPE_CONSUMPTION, itemCode));
        ((TextView) itemView.findViewById(R.id.quantity_recycler_item)).setText(String.format(getString(R.string.item_quantity), 1004));
        arrowImageView = itemView.findViewById(R.id.happiness_arrow_item);

        isHappy = true;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // initialize timer
        timerTask = new TimerTask() {
            @Override
            public void run() {
                isHappy = !isHappy;

                arrowImageView.setImageResource(isHappy ? R.drawable.icon_arrow_up : R.drawable.icon_arrow_down);
                arrowImageView.setColorFilter(ContextCompat.getColor(context, isHappy ? R.color.color_accent : R.color.color_accent_blue));
            }
        };

        // initialize timer
        timer = new Timer();
        timer.schedule(timerTask, 500, 500);
    }

    @Override
    public void onPause() {
        super.onPause();

        // cancel timer task
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // cancel timer
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        isHappy = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
