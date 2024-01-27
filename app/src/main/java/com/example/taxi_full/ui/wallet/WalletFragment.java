package com.example.taxi_full.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.taxi_full.API.adaptors.AdaptorWallet;
import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootAllWallet;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentWalletBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class WalletFragment extends Fragment {

    private FragmentWalletBinding binding;
    private final String URL_API = "http://45.86.47.12/api/wallet";
    private ListView list = null;
    private int c = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWalletBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        ImageButton back = root.findViewById(R.id.backWalletDriver);

        back.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.HomeDriver")));

        list = root.findViewById(R.id.listViewWallet);
        TextView allIncComingDriver = root.findViewById(R.id.allIncomingWallet);
        TextView notNalIncomingDriver = root.findViewById(R.id.off_nal_IncomingWallet);
        Button reject = root.findViewById(R.id.take_off_offNal_wallet);

        reject.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.CardPayDriver")));

        new Thread(()-> {
            DBClass db = new DBClass();
            String hash = db.getHash(root.getContext());
            String url = URL_API + "/" + hash;
            try {
                if(!HttpApi.getId(url).equals("<br />")) {
                    Type listType = new TypeToken<List<RootAllWallet>>() {
                    }.getType();
                    List<RootAllWallet> wallet = new Gson().fromJson(HttpApi.getId(url), listType);
                    c = wallet.size();
                    String[][] data = new String[c][3];
                    double allInc = 0;
                    double nnInc = 0;
                    for (int i = 0; i < wallet.size(); i++) {

                        if (wallet.get(i).getMethood().equals("1"))
                            data[i][0] = "Наличный расчет";
                        else
                            data[i][0] = "Безналичный расчет";
                        data[i][1] = wallet.get(i).getInForTakeOff();
                        allInc += Double.parseDouble(wallet.get(i).getInForTakeOff());
                        if (wallet.get(i).getMethood().equals("2"))
                            nnInc += Double.parseDouble(wallet.get(i).getInForTakeOff());

                    }
                    double finalAllInc = allInc;
                    double finalNnInc = nnInc;
                    int allIncInt = (int) finalAllInc;
                    int nnIncInt = (int) finalNnInc;
                    if (nnIncInt == 0)
                        requireActivity().runOnUiThread(() -> reject.setVisibility(View.GONE));
                    requireActivity().runOnUiThread(() -> {
                        list.setAdapter(new AdaptorWallet(root.getContext(), c, data));
                        allIncComingDriver.setText(allIncInt + "p");
                        notNalIncomingDriver.setText(nnIncInt + "р");
                    });
                } else
                    requireActivity().runOnUiThread(() -> reject.setVisibility(View.GONE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
