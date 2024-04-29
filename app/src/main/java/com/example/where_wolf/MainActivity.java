package com.example.where_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleJoinButton();
        handleHostButton();

    }

    private void handleJoinButton(){
        Button join_button = (Button) findViewById((R.id.buttonJoin));
        join_button.setText("Join");
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
                startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
            }
        } );
    }

    private void connectToServer(){
        //TextView status = (TextView) findViewById(R.id.textConnectionStatus);
        String TAG = "MainActivity";
        new Thread(new Client()).start();

//        try {
//            Log.d(TAG, "Trying to connect...");
//            Socket socket = new Socket("10.0.2.2", 6061);
//            Log.d(TAG, "creating socket...");
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String response = in.readLine();
//            //status.setText("Connected!");
//            socket.close();
//        } catch (IOException e) {
//            //status.setText("Failed");
//            Log.e(TAG, "Error connecting to server: " + e.getMessage());
//            Log.e(TAG, "Error connecting to server: " + e.getMessage());
//            Log.e(TAG, "Error connecting to server: " + e.getMessage());
//        } catch (NetworkOnMainThreadException e){
//            e.printStackTrace();
//            System.out.println(e);
//            //Log.e(TAG, e);
//            //Log.e(TAG, "Error connecting to server: " + e.getMessage());
//        }

    }

    private void handleHostButton(){
        Button join_button = (Button) findViewById((R.id.buttonHostGame));
        TextView status = (TextView) findViewById(R.id.textConnectionStatus);
        join_button.setText("Host");
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Server the_server = new Server();
                new Thread(new Server()).start();
                status.setText("Server started");

            }
        } );
    }


    private class Client implements Runnable {

        @Override
        public void run() {
            String TAG = "MainActivity";
            try{
                int local_port = 6061;
                Log.d(TAG, "Trying to connect...");
                Socket socket = new Socket("10.0.2.2", 6061);
                Log.d(TAG, "created socket.");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d(TAG, "created reader");
                String response = in.readLine();
                Log.d(TAG, response);
                //status.setText("Connected!");
                Log.d(TAG, "connection successful");
                socket.close();

            } catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class Server implements Runnable {
        @Override
        public void run() {
            String TAG = "MainActivity";
            //TextView status = (TextView) findViewById(R.id.textConnectionStatus);
            try {
                int server_port = 6060;
                String server_ip = "10.0.2.15";
                ServerSocket the_socket = new ServerSocket();
                Log.d(TAG, "socket created");
                the_socket.bind(new InetSocketAddress(server_ip, server_port));
                Log.d(TAG, "server set up");


                while (true) {
                    Log.d(TAG, "waiting for a connection");
                    Socket clientSocket = the_socket.accept();
                    Log.d(TAG, "Accepted connection");
                    //String message = in.readLine();
                    String message = "zzz";
                    clientSocket.getOutputStream().write(message.getBytes());
                    //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    clientSocket.close();
                    Log.d(TAG, "Closed Connection");
                }

            } catch (Exception e){
                //statuss.setText("Didn't start server");
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
            }
        }
    }


}