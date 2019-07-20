package com.example.minisweeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    ImageButton[][] button;
    GridLayout gridLayout;
    Button restart;
    TextView remainingMineCount, timer;

    private int rowCount;
    private int columnCount;
    private long best_time;
    private int mineCount;
    int buttonClickCount = 0;
    int rem_mine_count;
    long start_time, millisecond_time, timeBuff;

    int mineArray[][];
    int resultArray[][];
    boolean lockedArray[][];

    int gridBackground[] = {R.drawable.grid_0, R.drawable.grid_1, R.drawable.grid_2, R.drawable.grid_3, R.drawable.grid_4,
            R.drawable.grid_5, R.drawable.grid_6, R.drawable.grid_7, R.drawable.grid_8};

    boolean isGameOver = false;
    int selected_theme;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(MineSweeperConstants.shared_preference_key, Context.MODE_PRIVATE);
        int selected_grid_size = sharedPreferences.getInt(MineSweeperConstants.grid_size_selector_key, 0);
        selected_theme = sharedPreferences.getInt(MineSweeperConstants.theme_key, 0);
        rowCount = MineSweeperConstants.row_count_array[selected_grid_size];
        columnCount = MineSweeperConstants.column_count_array[selected_grid_size];

        Intent intent = getIntent();
        best_time = intent.getLongExtra("BEST_TIME", Long.MAX_VALUE);

        SharedPreferenceHandler.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_game);

        button = new ImageButton[rowCount+1][columnCount+1];
        mineArray = new int[rowCount+2][columnCount+2];
        resultArray = new int[rowCount+2][columnCount+2];
        lockedArray = new boolean[rowCount+2][columnCount+2];
        mineCount = (int) (0.15 * rowCount * columnCount);
        rem_mine_count = mineCount;

        for(int i=1; i<=rowCount; i++)
            for(int j=1; j<=columnCount; j++)
                button[i][j] = new ImageButton(this);

        remainingMineCount = (TextView) findViewById(R.id.mine_count);
        gridLayout = (GridLayout) findViewById(R.id.grid);
        timer = (TextView) findViewById(R.id.timer);
        gridLayout.setRowCount(rowCount);
        gridLayout.setColumnCount(columnCount);
        remainingMineCount.setText(mineCount+"");

        for(int i=1; i<=rowCount; i++){
            for(int j=1; j<=columnCount; j++){
                button[i][j] = new ImageButton(this);
                button[i][j].setTag(i+"_"+j);
                button[i][j].setId((i-1)*rowCount + j);
                button[i][j].setBackground(getDrawable(R.drawable.grid_button));
                button[i][j].setOnClickListener(this);
                button[i][j].setOnLongClickListener(this);
                gridLayout.addView(button[i][j]);
            }
        }
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            @Override
            public void onGlobalLayout() {
                int gridWidth = gridLayout.getWidth();
                int width = gridWidth/columnCount;
                int height = gridWidth/columnCount;
                int margin = 5;
                for(int i=1; i<=rowCount; i++){
                    for(int j=1; j<=columnCount; j++){
                        GridLayout.LayoutParams params = (GridLayout.LayoutParams) button[i][j].getLayoutParams();
                        params.width = width - 2*margin;
                        params.height = height - 2*margin;
                        params.setMargins(margin, margin, margin, margin);
                        button[i][j].setLayoutParams(params);
                    }
                }
            }
        });
        //initialize mine array
        generateMine();
        handler = new Handler();
        timeBuff = 0;

        restart = (Button) findViewById(R.id.restart_button);
        restart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                intent.putExtra("ROW_COUNT", rowCount);
                intent.putExtra("COLUMN_COUNT", columnCount);
                intent.putExtra("BEST_TIME", best_time);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        start_time = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 1000);
    }
    @Override
    protected void onStop() {
        super.onStop();
        timeBuff += millisecond_time;
        handler.removeCallbacks(runnable);
    }
    void generateMine(){
        for(int[] i : mineArray){
            Arrays.fill(i, 0);
        }
        for(int i=1; i<=mineCount; i++){
            int row = 0;
            while(row == 0){
                row = (int)(Math.random()*100) % rowCount;
            }
            int col = 0;
            while(col == 0){
                col = (int)(Math.random()*100) % columnCount;
            }
            while(mineArray[row][col] == 1 || row == 0 || col == 0){
                row = (int)(Math.random()*100) % rowCount;
                col = (int)(Math.random()*100) % columnCount;
            }
            mineArray[row][col] = 1;
        }
        int sum = 0;
        for(int i=1; i<=rowCount; i++){
            for(int j=1; j<=columnCount; j++){
                if(mineArray[i][j] != 1){
                    sum = mineArray[i-1][j] + mineArray[i-1][j-1] + mineArray[i-1][j+1];
                    sum	+= mineArray[i][j-1] + mineArray[i][j+1];
                    sum += mineArray[i+1][j-1] + mineArray[i+1][j] + mineArray[i+1][j+1];
                    resultArray[i][j] = sum;
                }else{
                    resultArray[i][j] = -1;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        ImageButton selectedBtn = (ImageButton) v;
        String selectedTag = (String) v.getTag();
        String subString[] = selectedTag.split("_");
        int i = convertStringtoInteger(subString[0]);
        int j = convertStringtoInteger(subString[1]);
        if(!isGameOver && lockedArray[i][j] == false){
            if(resultArray[i][j] == -1){
                isGameOver = true;
                openMineGrids();
                handler.removeCallbacks(runnable);
                restart.setVisibility(View.VISIBLE);
            }else if(resultArray[i][j] == 0){
                openSurroundingZero(i, j);
            }else{
                selectedBtn.setBackground(ContextCompat.getDrawable(this, gridBackground[resultArray[i][j]]));
                buttonClickCount++;
            }
            v.setEnabled(false);
            if(buttonClickCount == (rowCount * columnCount) - mineCount){
                openMineGrids();
                handler.removeCallbacks(runnable);
                timeBuff += millisecond_time;
                timeBuff /= 1000;
                DatabaseHelper helper = new DatabaseHelper(this);
                if(best_time == Long.MAX_VALUE){
                    if(!helper.insertBestTime(rowCount, columnCount, timeBuff)){
                        Toast.makeText(this, "Error Storing Best Time", Toast.LENGTH_LONG).show();
                    }
                }else if(timeBuff < best_time){
                    if(!helper.updateBestTime(rowCount, columnCount, timeBuff)){
                        Toast.makeText(this, "Error Storing Best Time", Toast.LENGTH_LONG).show();
                    }
                }
                restart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(rem_mine_count > 0){
            ImageButton selectedBtn = (ImageButton) v;
            String selectedTag = (String) v.getTag();
            String subString[] = selectedTag.split("_");
            int i = convertStringtoInteger(subString[0]);
            int j = convertStringtoInteger(subString[1]);
            if(lockedArray[i][j] == true){
                selectedBtn.setBackground(getDrawable(R.drawable.grid_button));
                lockedArray[i][j] = false;
                remainingMineCount.setText(++rem_mine_count+"");
            }else{
                selectedBtn.setBackground(getDrawable(R.drawable.locked_grid_button));
                lockedArray[i][j] = true;
                remainingMineCount.setText(--rem_mine_count+"");
            }
        }
        return true;
    }
    int convertStringtoInteger(String str){
        int len = str.length();
        int res = 0;
        int index = 0;
        int zero = (int)'0';
        while(len-- > 0){
            res = res + str.charAt(index) - zero;
            if(len > 0){
                res *= 10;
            }
            index++;
        }
        return res;
    }

    void openSurroundingZero(int rowNumber, int colNumber){
        for(int i=rowNumber-1; i<=rowNumber+1; i++){
            for(int j=colNumber-1; j<=colNumber+1; j++){
                ImageButton bt = (ImageButton) findViewById((i-1)*rowCount + j);
                if(i > 0 && i <= rowCount && j > 0 && j<=columnCount && bt.isEnabled()){
                    bt.setBackground(ContextCompat.getDrawable(this, gridBackground[resultArray[i][j]]));
                    buttonClickCount++;
                    bt.setEnabled(false);
                    if(resultArray[i][j] == 0 && (i != rowNumber || j != colNumber)){
                        openSurroundingZero(i, j);
                    }
                }
            }
        }
    }

    void openMineGrids(){
        for(int i=1; i<=rowCount; i++){
            for(int j=1; j<=columnCount; j++){
                ImageButton bt = (ImageButton) findViewById((i-1)*rowCount + j);
                if(bt.isEnabled()){
                    if(resultArray[i][j] == -1){
                        bt.setBackground(getDrawable(R.drawable.locked_grid_button));
                    }else{
                        bt.setBackground(getDrawable(gridBackground[resultArray[i][j]]));
                    }
                    bt.setEnabled(false);
                }
            }
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecond_time = SystemClock.uptimeMillis() - start_time;
            long update_time = timeBuff + millisecond_time;
            long seconds = update_time / 1000;
            long minutes = seconds/60;
            seconds = seconds % 60;
            timer.setText(""+minutes+":"+String.format("%02d", seconds));
            handler.postDelayed(this, 1000);
        }
    };

}