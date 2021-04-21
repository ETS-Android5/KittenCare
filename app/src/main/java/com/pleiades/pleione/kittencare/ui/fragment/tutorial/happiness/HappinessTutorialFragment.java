package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;

import java.util.Locale;

public class HappinessTutorialFragment extends Fragment {
    private Context context;

    private ProgressBar happinessProgressBar;
    private TextView happinessProgressTextView;
    private ValueAnimator progressValueAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_happiness, container, false);

        happinessProgressBar = rootView.findViewById(R.id.happiness_progress_tutorial);
        happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, R.drawable.drawable_progress_blue));
        happinessProgressBar.setProgress(0);

        happinessProgressTextView = rootView.findViewById(R.id.happiness_tutorial_happiness);
        happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", 0));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // initialize progress value animator
        progressValueAnimator = ValueAnimator.ofInt(0, 100);
        progressValueAnimator.setDuration(1000);
        progressValueAnimator.setInterpolator(new AccelerateInterpolator());
        progressValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, progress >= 50 ? R.drawable.drawable_progress : R.drawable.drawable_progress_blue));
                happinessProgressBar.setProgress(progress);
                happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", progress));
            }
        });

        // start progress value animtor
        progressValueAnimator.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (progressValueAnimator != null) {
            progressValueAnimator.removeAllUpdateListeners();
            progressValueAnimator.cancel();
        }

        happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, R.drawable.drawable_progress_blue));
        happinessProgressBar.setProgress(0);
        happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", 0));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
