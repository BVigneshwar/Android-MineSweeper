package com.example.minisweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button;
    TextView grid_size_selector;
    String grid_size_array[] = {"7 x 5", "10 x 7", "12 x 9", "13 x 11"};
    int size_selector_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_button = (Button) findViewById(R.id.start_button);
        grid_size_selector = (TextView) findViewById(R.id.grid_size_selector);

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

                            }
                        }
                        if(x1 > x2){
                            //right to left slide
                            if(size_selector_index < grid_size_array.length -1){

                            }
                        }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
}