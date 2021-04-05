package com.pleiades.pleione.kittencare.ui.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.pleiades.pleione.kittencare.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // initialize view pager 2
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(this);
        ViewPager2 viewPager2 = view.findViewById(R.id.pager_home);
        viewPager2.setAdapter(mainPagerAdapter);
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // initialize a new fragment
            if (position == 0)
                return new StatusFragment();
            else
                return new GameFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}