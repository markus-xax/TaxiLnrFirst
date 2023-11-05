package com.example.taxi_full.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_full.API.AdaptorHistory;
import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootAllOrders;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentGalleryBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GalleryFragment extends Fragment {

    private final String URL_API = "http://45.86.47.12/api/ordersHistory";

    private FragmentGalleryBinding binding;
    public ListView list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        list = root.findViewById(R.id.listViewHistory);


        new Thread(()->{
            DBClass db = new DBClass();
            String hash = db.getHash(getContext());
            String url = URL_API + "/" + hash;
            try {
                Type listType = new TypeToken<List<RootAllOrders>>() {
                }.getType();
                List<RootAllOrders> orders = new Gson().fromJson(HttpApi.getId(url), listType);
                String[][] data = new String[orders.size()][4];
                for(int i = 0; i< orders.size(); i++) {
                    data[i][0] = orders.get(i).getNameUser();
                    data[i][1] = orders.get(i).getNameDriver();
                    data[i][2] = orders.get(i).getStart_string();
                    data[i][3] = orders.get(i).getFinish_string();
                }
                requireActivity().runOnUiThread(() -> list.setAdapter(new AdaptorHistory(root.getContext(), orders.size(), data)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        ImageButton exit = root.findViewById(R.id.backGallery);

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