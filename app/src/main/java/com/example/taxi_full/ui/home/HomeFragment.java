package com.example.taxi_full.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);
        FrameLayout tp = view.findViewById(R.id.topPanel);
        FrameLayout bp = view.findViewById(R.id.bp);
        Button button = view.findViewById(R.id.button);

        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // этот код скрывает кнопку сразу же
                // и отображает после того как нижний экран полностью свернется
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    tp.setVisibility(View.GONE);
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    tp.setVisibility(View.VISIBLE);
                }

                bp.animate().alpha(1).setDuration(300).start();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if(tp.getVisibility() == View.GONE){
                    tp.setVisibility(View.VISIBLE);
                }
                tp.animate().alpha(1 - slideOffset).setDuration(0).start();
                bp.animate().alpha(1 - slideOffset).setDuration(0).start();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}