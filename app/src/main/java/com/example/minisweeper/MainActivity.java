package com.example.minisweeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button, prev_grid_size_button, next_grid_size_button;
    TextView grid_size_selector, best_time_display;
    Spinner theme_selector;

    int size_selector_index;
    long best_time = Long.MAX_VALUE;
    static int selected_theme;
    SharedPreferences sharedPreferences;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(MineSweeperConstants.shared_preference_key, Context.MODE_PRIVATE);
        selected_theme = sharedPreferences.getInt(MineSweeperConstants.theme_key, 0);
        size_selector_index = sharedPreferences.getInt(MineSweeperConstants.grid_size_selector_key, 0);

        ThemeChanger.onActivityCreateSetTheme(this, selected_theme);
        setContentView(R.layout.activity_main);

        start_button = (Button) findViewById(R.id.start_button);
        grid_size_selector = (TextView) findViewById(R.id.grid_size_selector);
        prev_grid_size_button = (Button) findViewById(R.id.prev_grid);
        next_grid_size_button = (Button) findViewById(R.id.next_grid);
        best_time_display = (TextView) findViewById(R.id.best_time);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        theme_selector = (Spinner) navigationView.getMenu().findItem(R.id.nav_theme).getActionView();
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
        theme_selector.setSelection(selected_theme, false);
        theme_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.selected_theme != position){
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt(MineSweeperConstants.theme_key, position);
                    edit.apply();
                    MainActivity.this.finish();
                    MainActivity.this.startActivity(new Intent(MainActivity.this, MainActivity.class));
                    MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        MenuModel menuModel = new MenuModel("Theme", true, false); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Dark", false, false);
        childModelsList.add(childModel);
        childModel = new MenuModel("Green", false, false);
        childModelsList.add(childModel);
        childList.put(menuModel, childModelsList);
    }

    private void populateExpandableList() {
        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {

                    }
                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);

                }

                return false;
            }
        });
    }
}