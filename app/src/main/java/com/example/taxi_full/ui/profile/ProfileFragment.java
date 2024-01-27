package com.example.taxi_full.ui.profile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.view.DBHelper;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmrntProfileBinding;
import com.google.gson.Gson;

import java.io.IOException;

public class ProfileFragment extends Fragment {
    private FragmrntProfileBinding binding;

    SQLiteDatabase db;
    int DC;
    String hash;
    public String URL_API = "http://45.86.47.12/api/user/";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        DBHelper dbHelper = new DBHelper(view.getContext());

        TextView name_surname = (TextView) view.findViewById(R.id.name_surname);
        EditText name = (EditText) view.findViewById(R.id.name);
        EditText surname = (EditText) view.findViewById(R.id.surname);
        EditText email = (EditText) view.findViewById(R.id.email);
        EditText phone = (EditText) view.findViewById(R.id.telephone);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);
        TextView rate = binding.getRoot().findViewById(R.id.rateProfileUser);

        if (cursor.moveToFirst()) {
            int IdIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int DcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
            int HashIndex = cursor.getColumnIndex(DBHelper.KEY_TOKEN);
            int ActiveIndex = cursor.getColumnIndex(DBHelper.KEY_ACTIVE);
            do {
                if (cursor.getInt(ActiveIndex) == 1) {
                    DC = cursor.getInt(DcIndex);
                    hash = cursor.getString(HashIndex);
                }
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");

        String url = URL_API+hash+"/"+DC;
        new Thread(() -> {
            Gson parser = new Gson();
            try {
                RootUserOne rootU = parser.fromJson(HttpApi.getId(url), RootUserOne.class);
                requireActivity().runOnUiThread(() -> {
                    name_surname.setText(rootU.getName() + " " + rootU.getSurname());
                    name.setText(rootU.getName());
                    surname.setText(rootU.getSurname());
                    phone.setText(rootU.getPhoneTrim());
                    email.setText(rootU.getEmail());
                    if(rootU.getRate() == null || rootU.getRate().equals(""))
                        rate.setText("5");
                    else
                        rate.setText(rootU.getRate());
                });

            } catch (IOException e) {
                Log.d("IOE-ex", e.getMessage());
            }

        }).start();

    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel infoViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmrntProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView16;
        infoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ImageButton exit = root.findViewById(R.id.backProfile);

        exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.Home")));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
