package com.example.minisweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{
    EditText edt_name, edt_comments;
    Button submitBtn;
    RatingBar ratingBar;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceHandler.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_feedback);
        database = FirebaseDatabase.getInstance().getReference();
        edt_name = (EditText) findViewById(R.id.fb_name);
        edt_comments = (EditText) findViewById(R.id.fb_comments);
        ratingBar = (RatingBar) findViewById(R.id.fb_rating);
        submitBtn = (Button) findViewById(R.id.fb_submit);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = edt_name.getText().toString();
        String comments = edt_comments.getText().toString();
        int rating = (int)ratingBar.getRating();
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            FeedbackHandler feedbackHandler = new FeedbackHandler(name, comments, rating);
            database.child("feedback").push().setValue(feedbackHandler);

            Dialog dialog = new Dialog(FeedbackActivity.this);
            dialog.setContentView(R.layout.feedback_dialog);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_container));
            Button close = dialog.findViewById(R.id.fb_close);
            close.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedbackActivity.this, MainActivity.class));
                    finish();
                }
            });
            dialog.show();
        }else{
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }

    }
}
