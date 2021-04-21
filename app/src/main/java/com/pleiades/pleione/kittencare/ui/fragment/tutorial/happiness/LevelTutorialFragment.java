package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;

import java.util.Locale;

public class LevelTutorialFragment extends Fragment {
    private Context context;
    private View rootView;

    private ProgressBar experienceProgressBar, happinessProgressBar;
    private TextView levelTextView, experienceProgressTextView, happinessProgressTextView;
    private ValueAnimator progressValueAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tutorial_level, container, false);

        // initialize components
        initializeComponents();

        return rootView;
    }

    private void initializeComponents() {
        experienceProgressBar = rootView.findViewById(R.id.experience_progress_tutorial_level);
        happinessProgressBar = rootView.findViewById(R.id.happiness_progress_tutorial_level);
        experienceProgressBar.setProgress(0);
        happinessProgressBar.setProgress(40);
        happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, R.drawable.drawable_progress_blue));

        levelTextView = rootView.findViewById(R.id.level_tutorial_level);
        levelTextView.setText(String.format(getString(R.string.block_label_level), 9));

        experienceProgressTextView = rootView.findViewById(R.id.experience_tutorial_level);
        happinessProgressTextView = rootView.findViewById(R.id.happiness_tutorial_level);
        experienceProgressTextView.setText(String.format(Locale.getDefault(), "%.2f %%", 0f));
        happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", 40));
    }

    private void animateExperienceUp() {
        // cancel animator
        cancelAnimator();

        // initialize progress value animator
        progressValueAnimator = ValueAnimator.ofFloat(0, 100);
        progressValueAnimator.setDuration(1000);
        progressValueAnimator.setInterpolator(new AccelerateInterpolator());
        progressValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                experienceProgressBar.setProgress((int) progress);
                experienceProgressTextView.setText(String.format(Locale.getDefault(), "%.2f %%", progress));
            }
        });
        progressValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate level up
                animateLevelUp();
            }
        });

        // start animation
        progressValueAnimator.start();
    }

    private void animateLevelUp() {
        // cancel animator
        cancelAnimator();

        // initialize progress value animator
        progressValueAnimator = ValueAnimator.ofInt(100, 0);
        progressValueAnimator.setDuration(500);
        progressValueAnimator.setInterpolator(new DecelerateInterpolator());
        progressValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                experienceProgressBar.setProgress(progress);
                happinessProgressBar.setProgress(100 - (progress * 6 / 10));
            }
        });
        progressValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                levelTextView.setText(String.format(getString(R.string.block_label_level), 10));
                experienceProgressTextView.setText(String.format(Locale.getDefault(), "%.2f %%", 0f));
                happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", 100));
                happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, R.drawable.drawable_progress));
            }
        });

        // start animation
        progressValueAnimator.start();
    }

    private void cancelAnimator() {
        // cancel animator
        if (progressValueAnimator != null) {
            progressValueAnimator.removeAllUpdateListeners();
            progressValueAnimator.removeAllListeners();
            progressValueAnimator.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // animate experience up
        animateExperienceUp();
    }

    @Override
    public void onPause() {
        super.onPause();

        // cancel animator
        cancelAnimator();

        // initialize components
        initializeComponents();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
