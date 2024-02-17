package com.example.taxi_full.view.home.user.anim;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.example.taxi_full.R;

public class AnimHome implements Anim {

    @Override
    public void hide(Context context, FrameLayout load) {
        Animation sunRiseAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_anim_close);
        load.startAnimation(sunRiseAnimation);
        load.setVisibility(View.GONE);
        Log.d("anim", "Start");
    }

    @Override
    public void start(Context context, FrameLayout load) {
        load.setVisibility(View.VISIBLE);
        Animation sunRiseAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_anim_start);
        load.startAnimation(sunRiseAnimation);
        Log.d("anim", "End");
    }
}
