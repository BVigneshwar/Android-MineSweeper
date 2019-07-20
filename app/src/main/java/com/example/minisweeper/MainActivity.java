package com.example.minisweeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button, prev_grid_size_button, next_grid_size_button;
    TextView grid_size_selector, best_time_display;

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

        SharedPreferenceHandler.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        start_button = (Button) findViewById(R.id.start_button);
        grid_size_selector = (TextView) findViewById(R.id.grid_size_selector);
        prev_grid_size_button = (Button) findViewById(R.id.prev_grid);
        next_grid_size_button = (Button) findViewById(R.id.next_grid);
        best_time_display = (TextView) findViewById(R.id.best_time);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        grid_size_selector.setText(MineSweeperConstants.row_count_array[size_selector_index]+" x "+MineSweeperConstants.column_count_array[size_selector_index]);
        if(size_selector_index == MineSweeperConstants.grid_size_array_count - 1){
            next_grid_size_button.setVisibility(View.INVISIBLE);
        }else if(size_selector_index == 0){
            prev_grid_size_button.setVisibility(View.INVISIBLE);
        }

        prepareMenuData();
        populateExpandableList();
        updateBestTime();

        start_button.setOnClickListener(this);

        grid_size_selector.setOnTouchListener(new View.OnTouchListener() {
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
                            if(size_selector_index > 0){
                                setPreviousGridSize();
                            }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(MineSweeperConstants.grid_size_selector_key, size_selector_index);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public void updateBestTime(){
        int row_num = MineSweeperConstants.row_count_array[size_selector_index];
        int col_num = MineSweeperConstants.column_count_array[size_selector_index];
        DatabaseHelper helper = new DatabaseHelper(this);
        Cursor cursor = helper.getRecord(row_num, col_num);
        if(cursor.moveToFirst()){
            best_time = cursor.getLong(0);
            int minutes = (int)(best_time/60);
            int seconds = (int)(best_time%60);
            best_time_display.setText(minutes+" : "+seconds);
        }else{
            best_time_display.setText("Good Luck !!!");
        }
    }

    private void prepareMenuData() {
        header_list.add(getResources().getString(R.string.theme));
        List<String> children_list = new ArrayList<>();
        children_list.add(getResources().getString(R.string.dark));
        children_list.add(getResources().getString(R.string.green));
        children_map.put(getResources().getString(R.string.theme), children_list);

        header_list.add(getResources().getString(R.string.sound));

        header_list.add(getResources().getString(R.string.vibration));

        header_list.add(getResources().getString(R.string.language));
        children_list = new ArrayList<>();
        children_list.add("English");
        children_list.add("French");
        children_list.add("German");
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