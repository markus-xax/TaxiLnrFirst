package com.example.taxi_full.API;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.R;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class AdaptorOrders extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private WebSocketClient mWebSocketClient;
    Context context;
    String[][] data;
    int[] dataImg;
    public final String URL_API_ONE = "http://45.86.47.12/api/order";
    private final String URL_API_USER = "http://45.86.47.12/api/user";
    private final String URL_CARS = "http://45.86.47.12/api/cars";
    private final String URL_WALLET = "http://45.86.47.12/api/wallet";
    private final String URL_DEBT = "http://45.86.47.12/api/debt";
    Map<String, String> nameOrder = new HashMap<>();
    int active = 1;

    public AdaptorOrders(Context context, String[][] data, int[] dataImg){
        this.context = context;
        this.data = data;
        this.dataImg = dataImg;

        //connectToSocket();

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = inflater.inflate(R.layout.element_home_driver_list, null);
        TextView nameSurname = v.findViewById(R.id.name_surname);
        TextView distance = v.findViewById(R.id.distance);
        TextView price = v.findViewById(R.id.price);
        EditText addressStart = v.findViewById(R.id.addressStart);
        EditText addressFinish = v.findViewById(R.id.addressFinish);
        ImageView profile = v.findViewById(R.id.profile_pictire);
        Button type_pay = v.findViewById(R.id.type_pay_driver_home);
        if(dataImg != null) {
            addressStart.setEnabled(false);
            addressFinish.setEnabled(false);
            nameSurname.setText(data[i][0]);
            distance.setText(data[i][1]);
            price.setText(data[i][2]);
            addressStart.setText(data[i][3]);
            addressFinish.setText(data[i][4]);
            if (data[i][5].equals("1"))
                type_pay.setText("Наличный расчет");
            else
                type_pay.setText("Безналичный расчет");
            profile.setImageResource(dataImg[i]);

            Button Button1 = v.findViewById(R.id.button5);
            Button1.setTag(i);
            new Thread(() -> {
                DBClass dbClass = new DBClass();
                String hash = dbClass.getHash(v.getContext());
                try {
                    if (!HttpApi.getId(URL_CARS + "/" + hash).equals("0")) {
                        Button1.setOnClickListener(view1 -> {
                            //отправка имени и фамилии заказчика при принятии заказа, отправка хэша водителя
                            int position = (Integer) view1.getTag();
                            DBClass db = new DBClass();
                            nameOrder.put("NameSurnameOrder", data[position][0]);
                            nameOrder.put("HashDriver", db.getHash(context));
                            JSONObject jsonGeometry = new JSONObject(nameOrder);
                            mWebSocketClient.send(String.valueOf(jsonGeometry));
                            mWebSocketClient.close();
                            DBClass dBClass = new DBClass();
                            String hashU = null;
                            try {
                                hashU = HttpApi.getId(URL_API_ONE + "/" + nameOrder.get("NameSurnameOrder"));
                            } catch (IOException e) {
                                Log.d("IOE-ex", e.getMessage());
                            }
                            if (hashU != null) {
                                String urlUser = URL_API_USER + "/" + dbClass.getHash(v.getContext()) + "/" + dbClass.getDC(v.getContext());
                                RootUserOne rootUserOne = null;
                                try {
                                    rootUserOne = new Gson().fromJson(HttpApi.getId(urlUser), RootUserOne.class);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                RootOrderOne r = new Gson().fromJson(hashU, RootOrderOne.class);
                                String url = URL_API_ONE + "/" + r.getHash_user();
                                String arg = "hashD=" + hash + "&nameD=" + rootUserOne.getNameSurname();
                                if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                                    // 1 - нал, 2 безнал для кошелка
                                    HttpApi.post(URL_WALLET, "hash=" + hash + "&active=" + active + "&methood=" + r.getType_pay());
                                    if (active == 2)
                                        HttpApi.post(URL_DEBT, "hash=" + hash);
                                    try {
                                        CityDriver.city = null;
                                        context.startActivity(new Intent("com.example.taxi_full.GoDriver"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else
                                    Log.d("err", hashU);
                            }

                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        return v;
    }

    @Override
    public int getCount() {
        if(dataImg != null)
            return dataImg.length;
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private void connectToSocket() {

        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:27810");
        } catch (URISyntaxException e) {
            Log.d("----uri------",e.getMessage());
            return;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
            }
            @Override
            public void onMessage(String s) {
                final String message = s;
            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);

            }
            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}