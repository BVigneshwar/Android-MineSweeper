package com.example.minisweeper;

import android.app.Activity;

public class ThemeChanger {
    static void onActivityCreateSetTheme(Activity activity, int theme){
        switch (theme){
            case 0:
                activity.setTheme(R.style.Dark);
                break;
            case 1:
                activity.setTheme(R.style.Green);
                break;
        }
    }
}
