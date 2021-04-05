package com.pleiades.pleione.kittencare.ui.fragment.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;

import static com.pleiades.pleione.kittencare.Config.ARGUMENT_KEY_POSITION;

public class MiddleTutorialFragment extends Fragment {
    private int position;

    public static MiddleTutorialFragment newInstance(int position) {
        MiddleTutorialFragment fragment = new MiddleTutorialFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_KEY_POSITION, position);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) {
            position = 1;
            return;
        }

        position = arguments.getInt(ARGUMENT_KEY_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;

        if (position == 1) {
            rootView = inflater.inflate(R.layout.fragment_tutorial_middle_1, container, false);
            rootView.findViewById(R.id.image_tutorial_middle_1).setClipToOutline(true);
        } else if (position == 2) {
            rootView = inflater.inflate(R.layout.fragment_tutorial_middle_2, container, false);
            rootView.findViewById(R.id.image_tutorial_middle_2).setClipToOutline(true);
        } else if (position == 3) {
            rootView = inflater.inflate(R.layout.fragment_tutorial_middle_3, container, false);
            rootView.findViewById(R.id.image_tutorial_middle_3).setClipToOutline(true);
        } else if (position == 4) {
            rootView = inflater.inflate(R.layout.fragment_tutorial_middle_4, container, false);
            rootView.findViewById(R.id.image_tutorial_middle_4).setClipToOutline(true);
        }

        return rootView;
    }
}
