package com.example.forum.Others;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Animation {


    public static void animation(Activity activity, int buttonId, int animId){
        Button button = activity.findViewById(buttonId);
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(activity, animId);
        button.startAnimation(animation);
    }

}
