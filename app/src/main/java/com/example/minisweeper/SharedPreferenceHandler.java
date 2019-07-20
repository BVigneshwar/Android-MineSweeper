package com.example.minisweeper;

import android.app.Activity;

public class SharedPreferenceHandler {
    static int selected_theme;
    static boolean isSoundEnable;
    static boolean isVibrationEnable;
    static void onActivityCreateSetTheme(Activity activity){
        switch (selected_theme){
            case 0:
                activity.setTheme(R.style.Dark);
                break;
            case 1:
                activity.setTheme(R.style.Green);
                break;
        }
    }
}
