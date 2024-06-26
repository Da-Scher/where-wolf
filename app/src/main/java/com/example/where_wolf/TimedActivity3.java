package com.example.where_wolf;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TimedActivity3 extends AppCompatActivity {

    private int duration = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed3);

        TextView seconds = findViewById(R.id.textViewSec3);

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
//                String resultData = "GODAY";
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("KEY_RESULT", resultData);
//                setResult(RESULT_OK, resultIntent);
//                finish();

                EditText input = (EditText) findViewById((R.id.editTextAttackTarget));
                ArrayList<String> resultData = new ArrayList<>();
                String resultData2 = input.getText().toString();
                String resultData1 = "GODAY";
                resultData.add(resultData1);
                resultData.add(resultData2);
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("string_list", resultData );
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }.start();
    }
}