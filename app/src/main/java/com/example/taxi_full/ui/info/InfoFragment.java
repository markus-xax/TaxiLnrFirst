package com.example.taxi_full.ui.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentInfoBinding;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        InfoViewModel infoViewModel =
                new ViewModelProvider(this).get(InfoViewModel.class);

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView12;
        infoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ImageButton exit = root.findViewById(R.id.backInfo);

        DBClass db = new DBClass();
        if(db.getDC(getContext()).equals("0"))
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.Home")));
        else
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.HomeDriver")));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}