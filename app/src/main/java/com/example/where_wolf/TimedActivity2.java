package com.example.where_wolf;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class TimedActivity2 extends AppCompatActivity {

    private int duration = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed2);

        TextView seconds = findViewById(R.id.textViewSec2);

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
                EditText input = (EditText) findViewById((R.id.editTextAccuseTarget));
                String resultData = input.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("KEY_RESULT", resultData);
                setResult(RESULT_OK, resultIntent);
                finish();
                //startActivity(new Intent(TimedActivity2.this, TimedActivity3.class));
            }
        }.start();

    }
}