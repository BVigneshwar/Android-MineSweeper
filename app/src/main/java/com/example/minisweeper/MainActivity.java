package com.example.minisweeper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    Button[][] button;
    GridLayout gridLayout;
    Button restart;

    private final int rowCount = 10;
    private final int columnCount = 7;
    private int mineCount = 10;
    int buttonClickCount = 0;

    int mineArray[][] = new int[rowCount+2][columnCount+2];
    int resultArray[][] = new int[rowCount+2][columnCount+2];
    boolean lockedArray[][] = new boolean[rowCount+2][columnCount+2];
    int gridBackground[] = {R.drawable.grid_0, R.drawable.grid_1, R.drawable.grid_2, R.drawable.grid_3, R.drawable.grid_4,
            R.drawable.grid_5, R.drawable.grid_6, R.drawable.grid_7, R.drawable.grid_8};

    boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = new Button[rowCount+1][columnCount+1];

        for(int i=1; i<=rowCount; i++)
            for(int j=1; j<=columnCount; j++)
                button[i][j] = new Button(this);

        gridLayout = (GridLayout) findViewById(R.id.grid);

        for(int i=1; i<=rowCount; i++){
            for(int j=1; j<=columnCount; j++){
                button[i][j] = new Button(this);
                button[i][j].setTag(i+"_"+j);
                button[i][j].setId((i-1)*rowCount + j);
                button[i][j].setBackground(getDrawable(R.drawable.grid_button));
                button[i][j].setTextColor(Color.WHITE);
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

        restart = (Button) findViewById(R.id.restart_button);
        restart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        Button selectedBtn = (Button) v;
        String selectedTag = (String) v.getTag();
        String subString[] = selectedTag.split("_");
        int i = convertStringtoInteger(subString[0]);
        int j = convertStringtoInteger(subString[1]);
        if(!isGameOver && lockedArray[i][j] == false){
            if(resultArray[i][j] == -1){
                isGameOver = true;
                openMineGrids();
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
                restart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Button selectedBtn = (Button) v;
        String selectedTag = (String) v.getTag();
        String subString[] = selectedTag.split("_");
        int i = convertStringtoInteger(subString[0]);
        int j = convertStringtoInteger(subString[1]);
        if(lockedArray[i][j] == true){
            selectedBtn.setBackground(getDrawable(R.drawable.grid_button));
            lockedArray[i][j] = false;
        }else{
            selectedBtn.setBackground(getDrawable(R.drawable.locked_grid_button));
            lockedArray[i][j] = true;
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
                Button bt = (Button) findViewById((i-1)*rowCount + j);
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
                Button bt = (Button) findViewById((i-1)*rowCount + j);
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

}
