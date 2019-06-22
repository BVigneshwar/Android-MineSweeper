package com.example.minisweeper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    Button[][] button;
    GridLayout gridLayout;

    final int rowCount = 10;
    final int columnCount = 7;
    int mineCount = 20;

    int mineArray[][] = new int[rowCount+2][columnCount+2];
    int resultArray[][] = new int[rowCount+2][columnCount+2];

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
                button[i][j].setTag(""+i+j);
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
        if(!isGameOver){
            Button selectedBtn = (Button) v;
            String selectedTag = (String) v.getTag();
            int subtract = (int)'0';
            int i = (int)selectedTag.charAt(0) - subtract;
            int j = (int)selectedTag.charAt(1) - subtract;
            selectedBtn.setTextColor(Color.WHITE);
            if(resultArray[i][j] == -1){
                Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
            }else{
                selectedBtn.setText(Integer.toString(resultArray[i][j]));
            }
            v.setEnabled(false);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Button selectedBtn = (Button) v;
        String selectedTag = (String) v.getTag();
        int subtract = (int)'0';
        int i = (int)selectedTag.charAt(0) - subtract;
        int j = (int)selectedTag.charAt(1) - subtract;

        selectedBtn.setText(Integer.toString(-1));
        return true;
    }

}
