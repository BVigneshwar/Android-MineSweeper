package com.example.minisweeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button, prev_grid_size_button, next_grid_size_button, prev_difficulty, next_difficulty;
    TextView grid_size_selector, best_time_display, difficulty_selector;
    LinearLayout grid_size_silder, difficulty_slider;
    DrawerLayout drawer;
    ImageButton settings;
    int size_selector_index;
    long best_time = Long.MAX_VALUE;
    SharedPreferences sharedPreferences;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> header_list = new ArrayList<>();
    Map<String, List<String>> children_map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(MineSweeperConstants.shared_preference_key, Context.MODE_PRIVATE);
        SharedPreferenceHandler.selected_theme = sharedPreferences.getInt(MineSweeperConstants.theme_key, 0);
        size_selector_index = sharedPreferences.getInt(MineSweeperConstants.grid_size_selector_key, 0);
        SharedPreferenceHandler.isSoundEnable = sharedPreferences.getBoolean(MineSweeperConstants.sound_key, true);
        SharedPreferenceHandler.isVibrationEnable = sharedPreferences.getBoolean(MineSweeperConstants.vibration_key, false);
        SharedPreferenceHandler.difficulty = sharedPreferences.getInt(MineSweeperConstants.difficulty_key, 1);

        SharedPreferenceHandler.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        start_button = (Button) findViewById(R.id.start_button);
        grid_size_selector = (TextView) findViewById(R.id.grid_size_selector);
        prev_grid_size_button = (Button) findViewById(R.id.prev_grid);
        next_grid_size_button = (Button) findViewById(R.id.next_grid);
        prev_difficulty = (Button) findViewById(R.id.prev_diff);
        next_difficulty = (Button) findViewById(R.id.next_diff);
        best_time_display = (TextView) findViewById(R.id.best_time);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        settings = (ImageButton) findViewById(R.id.settings);
        grid_size_silder = (LinearLayout) findViewById(R.id.grid_slider);
        difficulty_slider = (LinearLayout) findViewById(R.id.difficulty_slider);
        difficulty_selector = (TextView) findViewById(R.id.difficulty_selector);

        grid_size_selector.setText(MineSweeperConstants.row_count_array[size_selector_index]+" x "+MineSweeperConstants.column_count_array[size_selector_index]);
        difficulty_selector.setText(getResources().getString(MineSweeperConstants.difficulty_array[SharedPreferenceHandler.difficulty]));

        if(size_selector_index == MineSweeperConstants.grid_size_array_count - 1){
            next_grid_size_button.setVisibility(View.INVISIBLE);
        }else if(size_selector_index == 0){
            prev_grid_size_button.setVisibility(View.INVISIBLE);
        }

        if(SharedPreferenceHandler.difficulty == 2){
            next_difficulty.setVisibility(View.INVISIBLE);
        }else if(SharedPreferenceHandler.difficulty == 0){
            prev_difficulty.setVisibility(View.INVISIBLE);
        }

        prepareMenuData();
        populateExpandableList();
        updateBestTime();

        start_button.setOnClickListener(this);

        grid_size_silder.setOnTouchListener(new View.OnTouchListener() {
            float x1=0, y1=0, x2=0, y2=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        if(x1 < x2){
                            //left to right slide
                            setPreviousGridSize();
                        }
                        if(x1 > x2){
                            //right to left slide
                            setNextGridSize();
                        }
                }
                return true;
            }
        });

        prev_grid_size_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setPreviousGridSize();
            }
        });

        next_grid_size_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setNextGridSize();
            }
        });

        difficulty_slider.setOnTouchListener(new View.OnTouchListener(){
            float x1=0, y1=0, x2=0, y2=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        if(x1 < x2){
                            //left to right slide
                            setPreviousDifficulty();
                        }
                        if(x1 > x2){
                            //right to left slide
                            setNextDifficulty();
                        }
                }
                return true;
            }
        });
        prev_difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousDifficulty();
            }
        });
        next_difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDifficulty();
            }
        });
        settings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(MineSweeperConstants.grid_size_selector_key, size_selector_index);
        edit.putString(MineSweeperConstants.best_time_key, best_time_display.getText().toString());
        edit.putInt(MineSweeperConstants.difficulty_key, SharedPreferenceHandler.difficulty);
        edit.apply();

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("BEST_TIME", best_time);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateBestTime();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    void setPreviousGridSize(){
        if(size_selector_index > 0){
            grid_size_selector.setText(MineSweeperConstants.row_count_array[--size_selector_index]+" x "+MineSweeperConstants.column_count_array[size_selector_index]);
            if(size_selector_index == 0){
                prev_grid_size_button.setVisibility(View.INVISIBLE);
            }else{
                next_grid_size_button.setVisibility(View.VISIBLE);
            }
            updateBestTime();
        }
    }
    void setNextGridSize(){
        if(size_selector_index < MineSweeperConstants.grid_size_array_count -1){
            grid_size_selector.setText(MineSweeperConstants.row_count_array[++size_selector_index]+" x "+MineSweeperConstants.column_count_array[size_selector_index]);
            if(size_selector_index == MineSweeperConstants.grid_size_array_count - 1){
                next_grid_size_button.setVisibility(View.INVISIBLE);
            }else{
                prev_grid_size_button.setVisibility(View.VISIBLE);
            }
            updateBestTime();
        }
    }
    void setPreviousDifficulty(){
        if(SharedPreferenceHandler.difficulty > 0){
            difficulty_selector.setText(getResources().getString(MineSweeperConstants.difficulty_array[--SharedPreferenceHandler.difficulty]));
            if(SharedPreferenceHandler.difficulty == 0){
                prev_difficulty.setVisibility(View.INVISIBLE);
            }else{
                next_difficulty.setVisibility(View.VISIBLE);
            }
            updateBestTime();
        }
    }
    void setNextDifficulty(){
        if(SharedPreferenceHandler.difficulty < 2){
            difficulty_selector.setText(getResources().getString(MineSweeperConstants.difficulty_array[++SharedPreferenceHandler.difficulty]));
            if(SharedPreferenceHandler.difficulty == 2){
                next_difficulty.setVisibility(View.INVISIBLE);
            }else{
                prev_difficulty.setVisibility(View.VISIBLE);
            }
            updateBestTime();
        }
    }
    public void updateBestTime(){
        int row_num = MineSweeperConstants.row_count_array[size_selector_index];
        int col_num = MineSweeperConstants.column_count_array[size_selector_index];
        DatabaseHelper helper = new DatabaseHelper(this);
        Cursor cursor = helper.getRecord(row_num, col_num, SharedPreferenceHandler.difficulty);
        if(cursor.moveToFirst()){
            best_time = cursor.getLong(0);
            int minutes = (int)(best_time/60);
            int seconds = (int)(best_time%60);
            best_time_display.setText(minutes+" : "+seconds);
        }else{
            best_time_display.setText("0 : 00");
        }
    }

    private void prepareMenuData() {
        header_list.add(getResources().getString(R.string.theme));
        List<String> children_list = new ArrayList<>();
        children_list.add(getResources().getString(R.string.dark));
        children_list.add(getResources().getString(R.string.green));
        children_list.add(getResources().getString(R.string.red));
        children_list.add(getResources().getString(R.string.purple));
        children_list.add(getResources().getString(R.string.blue));
        children_list.add(getResources().getString(R.string.yellow));
        children_list.add(getResources().getString(R.string.cyan));
        children_list.add(getResources().getString(R.string.orange));
        children_map.put(getResources().getString(R.string.theme), children_list);

        header_list.add(getResources().getString(R.string.sound));

        header_list.add(getResources().getString(R.string.vibration));

        header_list.add(getResources().getString(R.string.language));
        children_list = new ArrayList<>();
        children_list.add(getResources().getString(R.string.english));
        children_list.add(getResources().getString(R.string.french));
        children_list.add(getResources().getString(R.string.german));
        children_map.put(getResources().getString(R.string.language), children_list);

        header_list.add(getResources().getString(R.string.feedback));

        header_list.add(getResources().getString(R.string.help));
    }

    private void populateExpandableList() {
        expandableListAdapter = new ExpandableListAdapter(this, header_list, children_map);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(groupPosition == 4){
                    drawer.closeDrawer(GravityCompat.END);
                    Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition == 0){
                    if(SharedPreferenceHandler.selected_theme != childPosition){
                        sharedPreferences = getSharedPreferences(MineSweeperConstants.shared_preference_key, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(MineSweeperConstants.theme_key, childPosition);
                        editor.putInt(MineSweeperConstants.difficulty_key, SharedPreferenceHandler.difficulty);
                        editor.apply();
                        MainActivity.this.finish();
                        MainActivity.this.startActivity(new Intent(MainActivity.this, MainActivity.class));
                        MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
                return false;
            }
        });
    }
}