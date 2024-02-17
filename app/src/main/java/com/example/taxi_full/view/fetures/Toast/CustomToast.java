package com.example.taxi_full.view.fetures.Toast;

import android.app.Activity;
import android.content.Context;

public class CustomToast implements Toast{

    @Override
    public void showToast(Activity activity, Context context, final String toast) {
        activity.runOnUiThread(() -> android.widget.Toast.makeText(context, toast, android.widget.Toast.LENGTH_LONG).show());
    }
}
