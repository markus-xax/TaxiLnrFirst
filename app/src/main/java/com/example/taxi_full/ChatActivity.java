package com.example.taxi_full;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.ChatArrayAdapter;
import com.example.taxi_full.API.ChatMessage;
import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.TCPSocket;
import com.example.taxi_full.API.model.RootChatRoom;
import com.example.taxi_full.API.model.RootChats;
import com.example.taxi_full.API.model.RootOrderOne;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private final boolean side = false;
    private final String URL_API = "http://45.86.47.12/api/chats/";
    private final String URL_API_ORDERS_TREE = "http://45.86.47.12/api/ordersThree";
    private DBClass dbClass = new DBClass();
    private String hash;
    private RootOrderOne r;
    private List<RootChats> chat;
    private final TCPSocket tcpSocket = new TCPSocket();
    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        ImageView buttonSend = findViewById(R.id.send);

        listView = findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = findViewById(R.id.sendMsg);
        chatText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                return sendChatMessage(chatText.getText().toString());
            }
            return false;
        });
        hash = dbClass.getHash(this);
        buttonSend.setOnClickListener(view -> {
            sendChatMessage(chatText.getText().toString());
        });

        connectToSocket();
        new Thread(()->{
            try {
                tcpSocket.TCP_Conn(19900);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        getChatMsg();

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        ImageButton exit = findViewById(R.id.backChat);
        DBClass db = new DBClass();
        if(db.getDC(this).equals("0"))
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.GoUser")));
        else
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.GoDriver")));
    }

    private boolean sendChatMessage(String text) {
        new Thread(()->{
            hash = dbClass.getHash(this);
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(()->{
                chatArrayAdapter.add(new ChatMessage(side, text));
                chatText.setText("");
            });
            HashMap<String, String> mes = new HashMap<>();

            if(dbClass.getDC(this).equals("0"))
                mes.put("user", r.getHash_driver());
            else
                mes.put("user", r.getHash_user());

            if(dbClass.getDC(this).equals("0"))
                mes.put("from", r.getHash_driver());
            else
                mes.put("from", r.getHash_user());

            mes.put("text", text);
            JSONObject jsonMsg = new JSONObject(mes);
            try {
                tcpSocket.send(jsonMsg);
            }catch (Exception e){
                Log.d("TCP", e.getMessage());
            }
            try{
                String arr = "hash_user="+hash+"&hash="+hash+"&text="+text;
                HttpApi.post(URL_API, arr);
            } catch (Exception e){
                e.printStackTrace();
            }

        }).start();
        return true;
    }

    private void getChatMsg(){
        hash = dbClass.getHash(this);
        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                String api = HttpApi.getId(URL_API + hash + "/" + r.getHash_driver());
                Log.d("api--", api);
                Type listType = new TypeToken<List<RootChats>>() {}.getType();
                if (dbClass.getDC(this).equals("0")){
                    try {
                        if(!api.equals("0")) {
                            chat = new Gson().fromJson(HttpApi.getId(URL_API + hash + "/" + r.getHash_driver()), listType);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if(!api.equals("0")) {
                            chat = new Gson().fromJson(HttpApi.getId(URL_API + hash + "/" + r.getHash_user()), listType);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        if(chat != null) {
            for (int i = 0; i < chat.size(); i++) {
                if (chat.get(i).getHash().equals(hash))
                    chatArrayAdapter.add(new ChatMessage(side, chat.get(i).getText()));
                else
                    chatArrayAdapter.add(new ChatMessage(!side, chat.get(i).getText()));
            }
        }

    }

    private void connectToSocket() {
        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:28900/?user="+hash);
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
                RootChatRoom m = new Gson().fromJson(s, RootChatRoom.class);
                if(m.getFrom().equals(hash))
                    chatArrayAdapter.add(new ChatMessage(side, m.getText()));
                else
                    chatArrayAdapter.add(new ChatMessage(!side, m.getText()));

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

    @Override
    protected void onStop() {
        new Thread(()->{
            try {
                tcpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        new Thread(()->{
            try {
                tcpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        super.onDestroy();
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