package com.example.where_wolf;

import androidx.annotation.UiContext;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Client the_client;

    private Handler handler = new Handler(msg -> {
        // Handle messages from the background thread
        if (msg.getData() != null) {
            Log.d("MainActivity", "AT HANDLER");

            String message = msg.getData().getString("key_message");
            Log.d("MainAcHandler", message);
            // Start the new activity here with the message
            if(message.equals("VD")){
                Intent intent = new Intent(MainActivity.this, TimedActivity.class);
                //intent.putExtra("EXTRA_MESSAGE", message);
                //startActivity(intent);
                startActivityForResult(intent, 6);
            }else if(message.equals("VV") || message.equals("WV")){
                Intent intent = new Intent(MainActivity.this, TimedActivity2.class);
                //intent.putExtra("EXTRA_MESSAGE", message);
                //startActivity(intent);
                startActivityForResult(intent, 6);
            }else if(message.equals("STARTGAME")){
                Intent intent = new Intent(MainActivity.this, TimedActivity2.class);
                //intent.putExtra("EXTRA_MESSAGE", message);
                startActivityForResult(intent, 6);
                //startActivity(intent);
            }
        }
        return true;
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "here");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6) {
            if (resultCode == RESULT_OK) {
                String resultData = data.getStringExtra("KEY_RESULT");
                Log.d("MainActivity", resultData);
                this.the_client.testing(resultData);
                Log.d("MainActivity", "Ran test");
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the case where the user canceled
            }
        }
    }

    static ArrayList<String> connectionsList;
    static ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView peerList = (ListView) findViewById((R.id.peerListView));
        handleJoinButton();
        handleHostButton();
        handleStartButton();

        connectionsList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, connectionsList);
        peerList.setAdapter(adapter);
    }

    private void handleStartButton() {
        Button startGameButton = (Button) findViewById((R.id.startGame));
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startGameButton.setText("StartingGame");
                //startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
            }
        } );
    }

    private void handleJoinButton(){
        Button join_button = (Button) findViewById((R.id.buttonJoin));
        join_button.setText("Join");
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
                //startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
            }
        } );
    }

    private void connectToServer(){
        TextView status = (TextView) findViewById(R.id.textConnectionStatus);
        EditText usernameBox = findViewById(R.id.usernameBox);
        String username = String.valueOf(usernameBox.getText());
        String TAG = "MainActivity:ConnectToServer";
        Client c = new Client(username, handler, "xxx");
        this.the_client = c;
        if(this.the_client == c){
            Log.d("MainAcitvity", "reference ok");
        }else{
            Log.d("MainAcitvity", "reference not  ok");
        }
        new Thread(c).start();
        status.setText("Client started");
    }

    private void handleHostButton(){
        Button join_button = (Button) findViewById((R.id.buttonHostGame));
        TextView status = (TextView) findViewById(R.id.textConnectionStatus);
        join_button.setText("Host");
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Server the_server = new Server();
                new Thread(new Server(handler, "xxx")).start();
                status.setText("Server started");

            }
        } );
    }


    public class Client implements Runnable {
        String resultMessage;

        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String username;
        private Handler handler;
        private ListView lv;
        private String message;
        private String role;

        public Client(String username, Handler handler, String message){
            this.lv = findViewById(R.id.peerListView);
            this.username = username;
            this.handler = handler;
            this.message = message;
            this.resultMessage="";
        }

        public void sendMessage() {
            try {
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                //TODO: get input from user here
                while (socket.isConnected()) {
                    if(!this.resultMessage.isEmpty()) {
                        String messageToSend = resultMessage;
                        bufferedWriter.write(username + ": " + messageToSend);
                        resultMessage = "";
                    }
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

        public void updateListView(String msg, ListView listView) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionsList.add(msg);
                    listView.setAdapter(adapter);
                }
            });
        }

        public void listenForMessage() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String TAG = "MainActivity:lisForMes";
                    String msgFromServer;
                    while (socket.isConnected()) {
                        try {

                            msgFromServer = bufferedReader.readLine();
                            Log.d(TAG, "MSGFROMSERVER: " + msgFromServer);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            message = msgFromServer;
                            bundle.putString("key_message", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            Log.d(TAG, "Handler MSG sent");
                            if(!msgFromServer.isEmpty())
                                Log.d(TAG, "MSGFROMSERVER: " + msgFromServer);{
                                char type = msgFromServer.charAt(0);
                                switch(type) {
                                    case 'P':
                                        connectionsList.clear();
                                        String rest = msgFromServer.substring(1);
                                        String list[] = rest.split(",");
                                        for (String name: list) {
                                            Log.e(TAG, "Member: " + name);
                                            updateListView(name, lv);
                                        }

                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (IOException e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            Log.d(TAG, "Error listen " + e.getMessage());
                        }
                    }
                }
            }).start();
        }

        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
            String TAG = "MainAct:Cli:clEveryth";
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

        public void testing(String str){
            Log.d("MainActivity", "THE RESULT:");
            Log.d("MainActivity", str);
            this.resultMessage = str;
        }

        @Override
        public void run() {
            String TAG = "MainActivityClient:Run";
            try {
                int local_port = 6061;
                Log.d(TAG, "Trying to connect...");
                socket = new Socket("10.0.2.2", 6061);
                this.socket = socket;
                Log.d(TAG, "created clientside socket.");
//                Client client = new Client(socket, username);
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d(TAG, "connection successful");
                listenForMessage();
                sendMessage();
                //status.setText("Connected!")
//                socket.close();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    public class Server implements Runnable {
        private Button startButton;
        private ServerSocket serverSocket;

        public ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

        public Server(Handler handler, String message) {
            this.serverSocket = serverSocket;
        }

//        public void startGame(){
//            new Thread(clientHandlers.get(0).broadcastMessage("STARTGAME")).start();
//        }

        @Override
        public void run() {
            String TAG = "MainActivity:Server:Run";
            //TextView status = (TextView) findViewById(R.id.textConnectionStatus);
            this.startButton = findViewById(R.id.startGame);
            this.startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clientHandlers.size() < 2) {
                        startButton.setText("NotEnoughPlayers");
                    } else {
                        startButton.setText("StartingGame");
                        clientHandlers.get(0).is_start = true;
//                    startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
                    }
                }
            } );
            try {
                int server_port = 6060;
                String server_ip = "10.0.2.15";
                Log.d(TAG, "socket created");
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(server_ip, server_port));
                Log.d(TAG, "server set up");

                while (!serverSocket.isClosed()) {
                    Log.d(TAG, "waiting for a connection");
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, "Accepted new connection");
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    Log.d(TAG, "End of Server Run Function");
                }

            } catch (Exception e){
                //status.setText("Didn't start server");
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
                Log.e(TAG, "Error starting server: " + e.getMessage());
            }
        }

        public void closeServerSocket() {
            String TAG = "MainActivity:ClServSock";
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
        boolean is_start;
        private Button startButton;

        ArrayList<ClientHandler> clientHandlers;
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        String clientUsername;

        public ClientHandler(Socket socket, ArrayList<ClientHandler> clientHandlers) {
            String TAG = "MainActivity:ClHand:Con";
            try {
                this.is_start=false;
                ListView lv = (ListView) findViewById(R.id.peerListView);
                this.clientHandlers = clientHandlers;
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientUsername = bufferedReader.readLine();
                clientHandlers.add(this);

                updateListView(clientUsername + " " +socket.getInetAddress().toString() + ":" + socket.getPort(), lv);
                String updateList = "P";
                Log.e(TAG, "Server: " + clientUsername + " has connected");
                int i = 0;
                for (ClientHandler clientHandler : clientHandlers) {
                    updateList += clientHandler.clientUsername + ",";
                    Log.e(TAG, "Server: " + "client Username" + i + " " + clientHandler.clientUsername);
                    i++;
                }
                Log.e(TAG, "updateList: " + updateList);
                broadcastMessage(updateList);
            } catch(IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
        @Override
        public void run() {
            String TAG = "MainAct:CliHand:Run";
            String messageFromClient;

            while (socket.isConnected()) {
                try {
                    messageFromClient = bufferedReader.readLine();
                    if(!(messageFromClient == null) && !(messageFromClient.isEmpty())){
                        Log.e(TAG, "message not empty");
                        broadcastMessage(messageFromClient);
                    } else if (is_start) {
                        broadcastMessage("STARTGAME");
                        is_start=false;
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }

        public void updateListView(String msg, ListView listView) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionsList.add(msg);
                    listView.setAdapter(adapter);
                }
            });
        }

        //broadcast a message to all clients
        public void broadcastMessage(String messageToSend) {
            String TAG = "MainActivity:BrCastMes";
            int i = 0;
            for (ClientHandler clientHandler : clientHandlers){
                Log.e(TAG, "Server: " + "client Username" + i + " " + clientHandler.clientUsername);
                i++;
                try {
                    //if (!clientHandler.clientUsername.equals(clientUsername)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    //}
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }

        public void removeClientHandler() {
            String TAG = "MainAct:rmCliHndlr";
            clientHandlers.remove(this);
            Log.e(TAG, "Server: " + clientUsername + " has disconnected");
        }

        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
            String TAG = "MainAct:CliHndl:clEvery";
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