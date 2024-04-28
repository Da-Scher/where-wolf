package com.example.where_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    WifiP2pManager manager;
    Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView.setText("Where-Wolf");
        textView2.setText("Hello");
        textView.setTextSize(64);
        textView2.setTextSize(32);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        handleJoinButton();

    }

    private void handleJoinButton(){
        Button join_button = (Button) findViewById((R.id.button2));
        //join_button.setText("Join Game");
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
                checkWifiP2pEnabled();
            }

        } );
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void checkWifiP2pEnabled() {
        manager.discoverPeers(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                textView2.setText("p2p ready");
            }

            @Override
            public void onFailure(int error) {
                textView2.setText("failed");
            }
        } );
    }



}
