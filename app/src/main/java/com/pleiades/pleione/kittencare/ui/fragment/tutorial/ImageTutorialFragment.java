package com.pleiades.pleione.kittencare.ui.fragment.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;

import static com.pleiades.pleione.kittencare.Config.ARGUMENT_KEY_POSITION;
import static com.pleiades.pleione.kittencare.Config.ARGUMENT_KEY_TYPE;
import static com.pleiades.pleione.kittencare.Config.TUTORIAL_TYPE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.TUTORIAL_TYPE_HAPPINESS;

public class ImageTutorialFragment extends Fragment {
    private int type, position;

    public static ImageTutorialFragment newInstance(int type, int position) {
        ImageTutorialFragment fragment = new ImageTutorialFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_KEY_TYPE, type);
        arguments.putInt(ARGUMENT_KEY_POSITION, position);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt(ARGUMENT_KEY_TYPE);
            position = arguments.getInt(ARGUMENT_KEY_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_image, container, false);
        ImageView imageView = rootView.findViewById(R.id.image_tutorial);
        TextView titleTextView = rootView.findViewById(R.id.title_tutorial_image);
        TextView contentsTextView = rootView.findViewById(R.id.contents_tutorial_image);
        int padding = getResources().getDimensionPixelSize(R.dimen.margin_very_small);

        if (type == TUTORIAL_TYPE_DEFAULT) {
            if (position == 1) {
                imageView.setBackgroundResource(R.drawable.drawable_round);
                imageView.setImageResource(R.drawable.image_tutorial_1);
                titleTextView.setText(R.string.tutorial_title_image_1);
                contentsTextView.setText(R.string.tutorial_contents_image_1);
            } else if (position == 2) {
                imageView.setBackgroundResource(R.drawable.background_round_stroke);
                imageView.setImageResource(R.drawable.image_tutorial_2);
                imageView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.padding_bottom_image_stroke));
                titleTextView.setText(R.string.tutorial_title_image_2);
                contentsTextView.setText(R.string.tutorial_contents_image_2);
            } else if (position == 3) {
                imageView.setBackgroundResource(R.drawable.background_round_stroke);
                imageView.setImageResource(R.drawable.image_tutorial_3);
                imageView.setPadding(padding, padding, padding, padding);
                titleTextView.setText(R.string.tutorial_title_image_3);
                contentsTextView.setText(R.string.tutorial_contents_image_3);
            } else if (position == 4) {
                imageView.setBackgroundResource(R.drawable.background_round_stroke);
                imageView.setImageResource(R.drawable.image_tutorial_4);
                imageView.setPadding(padding, padding, padding, padding);
                titleTextView.setText(R.string.tutorial_title_image_4);
                contentsTextView.setText(R.string.tutorial_contents_image_4);
            }
        } else if (type == TUTORIAL_TYPE_HAPPINESS) {
            // TODO
            if (position == 3) {
                titleTextView.setText(R.string.tutorial_title_daily);
                contentsTextView.setText(R.string.tutorial_contents_daily);
            } else if (position == 4) {
                titleTextView.setText(R.string.tutorial_title_happiness_image_3);
                contentsTextView.setText(R.string.tutorial_contents_happiness_image_3);
            } else if (position == 5) {
                titleTextView.setText(R.string.tutorial_title_happiness_image_4);
                contentsTextView.setText(R.string.tutorial_contents_happiness_image_4);
            }
        }

        imageView.setClipToOutline(true);
        return rootView;
    }
}
