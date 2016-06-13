package com.example.jaskirat.alternate_parking.util;

import android.view.View;
import android.widget.TextView;


public class ViewUtil {
    public static void setVisibility(int visibility, View... views) {
        for(View view: views) {
            if(view != null) {
                view.setVisibility(visibility);
            }
        }
    }

    public static void setTextColor(int color, TextView... views) {
        for(TextView view : views) {
            if(view != null) {
                view.setTextColor(color);
            }
        }
    }
}
