package com.example.where_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;

public class JoinGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        handle_back_button();
        handle_start_button();
    }

    private void handle_back_button(){
        Button back_button = (Button) findViewById(R.id.buttonBack);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        } );
    }

    private void handle_start_button(){
        Button start_button = (Button) findViewById(R.id.buttonStart);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(JoinGameActivity.this, TimedActivity.class));
                finish();
            }
        } );
    }




}