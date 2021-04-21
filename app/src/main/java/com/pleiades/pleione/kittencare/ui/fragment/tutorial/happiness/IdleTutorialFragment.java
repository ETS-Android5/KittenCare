package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;

public class IdleTutorialFragment extends Fragment {
    private Context context;

    private View leftKittenView, rightKittenView;
    private ImageView leftFaceImageView, rightFaceImageView;
    private ObjectAnimator leftObjectAnimator, rightObjectAnimator;
    private AnimatorSet animatorSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_idle, container, false);

        // initialize left kitten view
        leftKittenView = rootView.findViewById(R.id.kitten_left_tutorial_idle);
        leftFaceImageView = leftKittenView.findViewById(R.id.face_kitten);
        ((ImageView) leftKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) leftKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_alcyone);

        // initialize right kitten view
        rightKittenView = rootView.findViewById(R.id.kitten_right_tutorial_idle);
        rightFaceImageView = rightKittenView.findViewById(R.id.face_kitten);
        ((ImageView) rightKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) rightKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_pleione);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // animate both kittens
        animateKittensRotate();
    }

    private void animateKittensRotate() {
        // initialize rotate animation
        Animation leftRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_left);
        Animation rightRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right);

        leftFaceImageView.setImageResource(R.drawable.image_face_sleep);
        rightFaceImageView.setImageResource(R.drawable.image_face_sleep);

        leftKittenView.startAnimation(leftRotateAnimation);
        rightKittenView.startAnimation(rightRotateAnimation);
    }

    @Override
    public void onPause() {
        super.onPause();

        // cancel animator
        cancelAnimator();

        // reset both kittens
        leftFaceImageView.setImageResource(R.drawable.image_face_default);
        leftKittenView.setRotation(0);
        rightFaceImageView.setImageResource(R.drawable.image_face_default);
        rightKittenView.setRotation(0);
    }

    private void cancelAnimator() {
        if (leftKittenView != null)
            leftKittenView.clearAnimation();
        if (rightKittenView != null)
            rightKittenView.clearAnimation();
        if (leftObjectAnimator != null) {
            leftObjectAnimator.removeAllUpdateListeners();
            leftObjectAnimator.removeAllListeners();
            leftObjectAnimator.cancel();
        }
        if (rightObjectAnimator != null) {
            rightObjectAnimator.removeAllUpdateListeners();
            rightObjectAnimator.removeAllListeners();
            rightObjectAnimator.cancel();
        }
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.cancel();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
