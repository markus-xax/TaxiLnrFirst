package com.example.taxi_full.API.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taxi_full.R;

public class AdaptorHistory extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private int count = 0;
    private Context context;
    String[][] data;
    
    public AdaptorHistory (Context context, int count, String[][] data){
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
        final View v = inflater.inflate(R.layout.order_history, null);
        
        TextView name_surname = v.findViewById(R.id.nameSurnameOrders);
        TextView mark = v.findViewById(R.id.markCarOrders);
        TextView start = v.findViewById(R.id.startOrders);
        TextView finish = v.findViewById(R.id.finishOrders);
        
        name_surname.setText(data[i][0]);
        mark.setText(data[i][1]);
        start.setText(data[i][2]);
        finish.setText(data[i][3]);
        
        return v;
    }
}
