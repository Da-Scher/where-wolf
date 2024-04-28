package com.example.where_wolf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class Client {
    Socket sockClientSocket;
    String sIpAddress;
    int iPort;
    PlayerRole _prRole;
    PrintWriter pwOut;
    BufferedReader brIn;

    Client(String sIpAddress, int iPort) {
        sIpAddress = sIpAddress;
        iPort = iPort;
        sockClientSocket = clientConnect(sIpAddress, iPort);
        pwOut = clientOutput(sockClientSocket);
        brIn =  clientInput(sockClientSocket);
    }

    private Socket clientConnect(String sIpAddress, int iPort) {
        try {
            return new Socket(sIpAddress, iPort);
        } catch (IOException error) {
            System.out.println(error);
            return null;
        }
    }

    private PrintWriter clientOutput(Socket socket) {
        try {
            return new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException error) {
            System.out.println(error);
            return null;
        }
    }

    private BufferedReader clientInput(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException error) {
            System.out.println(error);
            return null;
        }
    }

    public String sendMessageToServer(String msg) {
        // TODO: generate a checksum for the message
        pwOut.println(msg);
        try {
             return brIn.readLine();
        } catch (IOException error) {
            return null;
        }
    }

    public PlayerRole sendJoinRequest() {
        pwOut.println("");
        try{
            // read the response from the server
            String response = brIn.readLine();
            // convert the response to JSON
            JSONObject joInput = new JSONObject(response);
            // create an object for the player role.
            // define the variables for the player role.
            String sJsonName = (String) joInput.get("sName");
            int iJsonId = (int) joInput.get("iId");
            boolean bJsonAlive = (boolean) joInput.get("bAlive");
            int iFaction = (int) joInput.get("iFaction");
            int iPowerTarget = (int) joInput.get("iPowerTarget");
            int iAccusationTarget = (int) joInput.get("iAccusationTarget");
            int iVoteTarget = (int) joInput.get("iVoteTarget");
            return new PlayerRole(sJsonName,
                    iJsonId,
                    bJsonAlive,
                    iFaction,
                    iPowerTarget,
                    iAccusationTarget,
                    iVoteTarget);
        } catch (IOException error) {
            error.printStackTrace();
            System.out.println(error);
            return null;
        } catch (JSONException error) {
            error.printStackTrace();
            System.out.println(error);
            return null;
        }
    }
}