package com.example.jaskirat.alternate_parking.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by pchekuri on 11/8/14.
 */
public class DisplayUtil {

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        dp = Math.round(px / displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static int dpToPx(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (displayMetrics.densityDpi / 160f);
        return (int) px;
    }

}