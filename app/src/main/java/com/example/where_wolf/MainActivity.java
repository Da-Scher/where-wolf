package com.example.where_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

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

    public class Server implements Runnable {

        private ServerSocket serverSocket;

        public Server(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            String TAG = "MainActivity";
            //TextView status = (TextView) findViewById(R.id.textConnectionStatus);
            try {
                int server_port = 6060;
                String server_ip = "10.0.2.15";
                //ServerSocket the_socket = new ServerSocket();
                Log.d(TAG, "socket created");
                this.serverSocket.bind(new InetSocketAddress(server_ip, server_port));
                Log.d(TAG, "server set up");

                while (!serverSocket.isClosed()) {
                    Log.d(TAG, "waiting for a connection");
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, "Accepted new connection");
                    ClientHandler clientHandler = new ClientHandler(clientSocket);

                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    //String message = in.readLine();
//                    String message = "zzz";
//                    clientSocket.getOutputStream().write(message.getBytes());
//                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

//                    clientSocket.close();
                    Log.d(TAG, "Closed Connection");
                }

            } catch (Exception e){
                //statuss.setText("Didn't start server");
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
            }
        }

        public void closeServerSocket() {
            String TAG = "MainActivity";
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            }catch (IOException e) {
                Log.e(TAG, "Error closing server socket: " + e.getMessage());
            }
        }
    }

    //creates a new thread and waits for messages from client sockets.
    //handles breadcasting messages back to client sockets and closing sockets.
    public class ClientHandler implements Runnable{
        public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        String clientUsername;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientUsername = bufferedReader.readLine();
                clientHandlers.add(this);
                Log.e(TAG, "Server: " + clientUsername + " has disconnected");
                //broadcastMessage();
            } catch(IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
        @Override
        public void run() {
            String messageFromClient;

            while (socket.isConnected()) {
                try {
                    messageFromClient = bufferedReader.readLine();
                    broadcastMessage(messageFromClient);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }

        //broadcast a message to all clients
        public void broadcastMessage(String messageToSend) {
            for (ClientHandler clientHandler : clientHandlers){
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }

        public void removeClientHandler() {
            String TAG = "MainActivity";
            clientHandlers.remove(this);
            Log.e(TAG, "Server: " + clientUsername + " has disconnected");
        }

        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
            String TAG = "MainActivity";
            removeClientHandler();
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "error closing a client handler");
            }
        }
    }
}