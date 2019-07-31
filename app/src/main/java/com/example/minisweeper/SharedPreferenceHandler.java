package com.example.minisweeper;

import android.app.Activity;

public class SharedPreferenceHandler {
    static int selected_theme;
    static boolean isSoundEnable;
    static boolean isVibrationEnable;
    static String best_time;
    static int difficulty;
    static void onActivityCreateSetTheme(Activity activity){
        switch (selected_theme){
            case 0:
                activity.setTheme(R.style.Dark);
                break;
            case 1:
                activity.setTheme(R.style.Green);
                break;
            case 2:
                activity.setTheme(R.style.Red);
                break;
            case 3:
                activity.setTheme(R.style.Purple);
                break;
            case 4:
                activity.setTheme(R.style.Blue);
                break;
            case 5:
                activity.setTheme(R.style.Yellow);
                break;
            case 6:
                activity.setTheme(R.style.Cyan);
                break;
            case 7:
                activity.setTheme(R.style.Orange);
                break;
        }
    }
}
