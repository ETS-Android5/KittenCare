package com.pleiades.pleione.kittencare.ui.fragment.tutorial;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DELAY_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_COMPLETE_TUTORIAL;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_TUTORIAL_COMPLETED;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.controller.AnimationController.calculateDurationGravity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

public class PawTutorialFragment extends Fragment {
    private Context context;

    private ImageView kittenPawImageView;
    private float kittenPawOriginPositionY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tutorial_paw, container, false);

        // initialize kitten paw image view
        kittenPawImageView = rootView.findViewById(R.id.paw_tutorial);
        kittenPawImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // initialize origin kitten paw position
                        kittenPawOriginPositionY = kittenPawImageView.getY();

                        // remove layout listener
                        kittenPawImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        kittenPawImageView.setOnClickListener(view -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

            // case is tutorial completed
            if (prefs.getBoolean(KEY_IS_TUTORIAL_COMPLETED, false)) {
                Activity parentActivity = getActivity();
                if (parentActivity != null)
                    parentActivity.onBackPressed();
            } else {
                // show complete tutorial dialog
                DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_COMPLETE_TUTORIAL);
                defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_COMPLETE_TUTORIAL));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (kittenPawOriginPositionY != 0.0f)
                animateKittenPawJumpUp();
        }, DELAY_DEFAULT);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void animateKittenPawJumpUp() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(kittenPawOriginPositionY, kittenPawOriginPositionY - DEFAULT_JUMP_DISTANCE);
        valueAnimator.setDuration(calculateDurationGravity(context, DEFAULT_JUMP_DISTANCE, false));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> kittenPawImageView.setY((Float) animation.getAnimatedValue()));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenPawJumpDown();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateKittenPawJumpDown() {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(kittenPawOriginPositionY - DEFAULT_JUMP_DISTANCE, kittenPawOriginPositionY);
        valueAnimator.setDuration(calculateDurationGravity(context, DEFAULT_JUMP_DISTANCE, false));
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> kittenPawImageView.setY((Float) animation.getAnimatedValue()));

        // start animation
        valueAnimator.start();
    }
}
