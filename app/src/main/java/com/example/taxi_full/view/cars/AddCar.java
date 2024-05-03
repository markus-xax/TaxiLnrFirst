package com.example.taxi_full.view.cars;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootAllMarkCars;
import com.example.taxi_full.API.model.RootAllModelCars;
import com.example.taxi_full.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddCar extends AppCompatActivity {

    String[] data = {"Седан", "Минивэн", "Джип", "Кроссовер", "Хэтчбек"};
    String[] dataColor = {"Белый","Черный", "Синий", "Зеленый", "Красный", "Желтый"};
    String[] dataClass = {"Эконом", "Стандарт", "Бизнес"};

    private final String URL_API = "http://45.86.47.12/api/cars";
    private final String URL_API_CARS = "http://45.86.47.12/api/AllCars/";
    private String hash;
    private DBClass dbClass;
    private String typeCar = "Седан";
    private String markCar = null;
    private String modelCar = null;
    private int colorCar = 1;
    private int classCar = 1;
    List<RootAllMarkCars> markCars = null;
    List<RootAllModelCars> modelCars = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_car);

        Button save = findViewById(R.id.save);

        Spinner mark = findViewById(R.id.mark);
        TextView number = findViewById(R.id.number);

        dbClass = new DBClass();
        hash = dbClass.getHash(this);

        spinnerCar();
        spinnerColor();
        spinnerMark();
        spinnerClass();

        save.setOnClickListener(view -> {
            if(markCar != null & modelCar != null) {
                String arr = "hash=" + hash + "&model=" + markCar + " " + modelCar + "&number=" + number.getText() + "&type=" + typeCar + "&color=" + colorCar + "&class=" + classCar;
                new Thread(() -> {
                    if (HttpApi.post(URL_API, arr) == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() ->
                                Toast.makeText(AddCar.this, "Машина успешно добавлена!", Toast.LENGTH_LONG).show());

                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(AddCar.this, "Произошла ошибка", Toast.LENGTH_LONG).show());
                    }
                }).start();

                startActivity(new Intent("com.example.taxi_full.HomeDriver"));
            }else
                Toast.makeText(this, "Выберите марку и модель автомобиля!", Toast.LENGTH_LONG).show();
        });

    }

    private void spinnerCar(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.type);
        spinner.setAdapter(adapter);

        spinner.setPrompt(getString(R.string.type_car));

        spinner.setSelection(2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                typeCar = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void spinnerMark() {
        try {
            Runnable getCars = () -> {
                Type listType = new TypeToken<List<RootAllMarkCars>>() {
                }.getType();
                try {
                    markCars = new Gson().fromJson(HttpApi.getId(URL_API_CARS), listType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(getCars);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> carsList = new ArrayList<>();
        for (int i = 0; i < markCars.size(); i++) {
            carsList.add(markCars.get(i).getMark());
        }
        String[] cars = carsList.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.mark);
        spinner.setAdapter(adapter);

        spinner.setPrompt("Марка");

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                markCar = null;
                markCar = spinner.getSelectedItem().toString();
                spinnerModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> findMarkWithPrefix(List<String> list, String prefix){
        if(prefix != null) {
            return list.stream()
                    .filter(str -> str.contains(prefix))
                    .collect(Collectors.toList());
        } else return list;
    }

    private void spinnerModel(){
        try {
            Runnable getCars = () -> {
                Type listType = new TypeToken<List<RootAllModelCars>>() {
                }.getType();
                try {
                    modelCars = new Gson().fromJson(HttpApi.getId(URL_API_CARS + markCar), listType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(getCars);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> modelsList = new ArrayList<>();
        for (int i = 0; i < modelCars.size(); i++) {
            modelsList.add(modelCars.get(i).getModel());
        }
        String[] models = modelsList.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.model);
        spinner.setAdapter(adapter);

        spinner.setPrompt("Модель");

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                modelCar = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void spinnerColor(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataColor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.color);
        spinner.setAdapter(adapter);

        spinner.setPrompt(getString(R.string.color));

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(spinner.getSelectedItem().toString().equals("Белый"))
                    colorCar = 1;
                if(spinner.getSelectedItem().toString().equals("Черный"))
                    colorCar = 2;
                if(spinner.getSelectedItem().toString().equals("Синий"))
                    colorCar = 3;
                if(spinner.getSelectedItem().toString().equals("Зеленый"))
                    colorCar = 4;
                if(spinner.getSelectedItem().toString().equals("Красный"))
                    colorCar = 5;
                if(spinner.getSelectedItem().toString().equals("Желтый"))
                    colorCar = 6;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void spinnerClass(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataClass);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.classCar);
        spinner.setAdapter(adapter);

        spinner.setPrompt("Класс");

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                classCar = position;
                classCar++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

}
