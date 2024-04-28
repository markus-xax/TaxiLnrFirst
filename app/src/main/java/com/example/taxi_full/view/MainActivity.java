package com.example.taxi_full.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        Thread logoTimer = new Thread()
        {
            public void run()
            {
                try
                {
                    int logoTimer = 0;
                    while(logoTimer < 1000)
                    {
                        sleep(100);
                        logoTimer = logoTimer +100;
                    };
                    startActivity(new Intent("com.example.taxi_full.Welcome"));
                }
                catch (InterruptedException e)
                {
                    // TODO: автоматически сгенерированный блок catch.
                    e.printStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        };
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);;
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        gps(enabled, logoTimer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);;
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Thread logoTimer = new Thread()
        {
            public void run()
            {
                try
                {
                    int logoTimer = 0;
                    while(logoTimer < 1000)
                    {
                        sleep(100);
                        logoTimer = logoTimer +100;
                    };
                    startActivity(new Intent("com.example.taxi_full.Welcome"));
                }
                catch (InterruptedException e)
                {
                    // TODO: автоматически сгенерированный блок catch.
                    e.printStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        };
        gps(enabled, logoTimer);
    }

    private void gps(boolean enabled, Thread logoTimer) {
        if(enabled)
            logoTimer.start();
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage(getResources().getString(R.string.msg_switch_network))
                    .setPositiveButton("Включить", (dialog, id) -> {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(dialog -> {
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.black));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();
            });
            alert.show();
        }
    }

}
