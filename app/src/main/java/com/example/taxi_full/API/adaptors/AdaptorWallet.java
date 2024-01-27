package com.example.taxi_full.API.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taxi_full.R;

public class AdaptorWallet extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private int count = 0;
    private Context context;
    String[][] data;

    public AdaptorWallet (Context context, int count, String[][] data){
        this.count = count;
        this.context = context;
        this.data = data;

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View v = inflater.inflate(R.layout.wallet_history, null);
        TextView name_surname = v.findViewById(R.id.nameSurnameWallet);
        TextView price = v.findViewById(R.id.price);
        name_surname.setText(data[i][0]);
        double pr = Double.parseDouble(data[i][1]);
        int prInt = (int) pr;
        price.setText(prInt + "р");

        return v;
    }
}
