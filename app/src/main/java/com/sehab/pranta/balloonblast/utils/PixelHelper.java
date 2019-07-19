package com.sehab.pranta.balloonblast.utils;

import android.content.Context;
import android.util.TypedValue;

public class PixelHelper {

    public static int pixelsToDp(int px, Context context) { //converting px to dp for density independent pixels
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px,
                context.getResources().getDisplayMetrics());
    }

}