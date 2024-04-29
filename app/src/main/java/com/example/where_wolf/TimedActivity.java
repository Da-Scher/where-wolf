package com.example.where_wolf;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class TimedActivity extends AppCompatActivity {

    private int duration = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed);

        TextView seconds = findViewById(R.id.textViewSec);

        new CountDownTimer(duration * 1000, 1000) {
            @Override
            public void onTick( long msToGo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String time = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(msToGo));
                        seconds.setText(time);
                    }
                } );
            }
            @Override
            public void onFinish(){
                //startActivity(new Intent(TimedActivity.this, MainActivity.class));
                String resultData = "This STR";
                Intent resultIntent = new Intent();
                resultIntent.putExtra("KEY_RESULT", resultData);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }.start();

    }






}