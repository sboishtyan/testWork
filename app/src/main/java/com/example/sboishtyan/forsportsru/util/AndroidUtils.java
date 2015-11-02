package com.example.sboishtyan.forsportsru.util;

import android.content.res.Configuration;

public final class AndroidUtils {
    public static String getScreenSizeCategory (int screenSizeCategory) {
        switch (screenSizeCategory) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "xlarge";
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "medium";
            default:
                return "xxlarge";
        }
    }
    private AndroidUtils(){}
}
