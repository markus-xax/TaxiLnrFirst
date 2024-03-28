package com.example.taxi_full.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentProfileDriverBinding;
import com.google.gson.Gson;

import java.io.IOException;

public class ProfileDriverFragment extends Fragment {

    private FragmentProfileDriverBinding binding;
    private final String URL_API_USERS = "http://45.86.47.12/api/user";
    private final String URL_CARS = "http://45.86.47.12/api/cars";
    private RootCars rootCars = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton imageButton = root.findViewById(R.id.imageButton);

        TextView name_surname = root.findViewById(R.id.nameSurnameProfileDriver);
        TextView rate = root.findViewById(R.id.rateProfileDriver);
        TextView model = root.findViewById(R.id.car_model_driver_profile);
        TextView number = root.findViewById(R.id.car_numbers_profile_driver);

        new Thread(()-> {
            DBClass dbClass = new DBClass();
            String hash = dbClass.getHash(root.getContext());
            String url = URL_API_USERS +"/"+hash+"/"+dbClass.getDC(root.getContext());
            try {
                requireActivity().runOnUiThread(()->{
                    imageButton.setBackgroundColor(Color.WHITE);
                    imageButton.setImageResource(R.drawable.car_driver_profile);
                });
                if (!HttpApi.getId(URL_CARS + "/" + hash).equals("0")) {
                    rootCars = new Gson().fromJson(HttpApi.getId(URL_CARS + "/" + hash), RootCars.class);
                }
                    RootUserOne rootUserOne = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                requireActivity().runOnUiThread(() -> {
                        if (rootUserOne.getError().equals(""))
                            name_surname.setText(rootUserOne.getNameSurname());
                        if (rootUserOne.getRate() == null || rootUserOne.getRate().equals(""))
                            rate.setText("5");
                        else
                            rate.setText(rootUserOne.getRate());
                    new Thread(() -> {
                        try {
                            if (!HttpApi.getId(URL_CARS + "/" + hash).equals("0")) {
                                if (rootCars.getError() == null) {
                                    requireActivity().runOnUiThread(()->{
                                        model.setText(rootCars.getModel());
                                        number.setText(rootCars.getNumber());
                                        imageButton.setEnabled(false);
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        imageButton.setOnClickListener(view -> {
            startActivity(new Intent("com.example.taxi_full.AddCar"));
        });

        ImageButton exit = root.findViewById(R.id.backProfileDriver);

        exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.HomeDriver")));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
