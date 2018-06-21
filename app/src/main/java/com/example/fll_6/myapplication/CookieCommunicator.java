package com.example.fll_6.myapplication;
import android.bluetooth.*;
import android.content.Intent;
import android.app.*;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CookieCommunicator {
    private BluetoothSocket socket;
    private InputStream inStream;
    private OutputStream outStream;

    private static final String TAG = "CookieCom";
    public boolean state = false;
    public CookieCommunicator() throws java.io.IOException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new RuntimeException("BT not supported!");
        }

        Log.i(TAG, "Got BT adapter");

        BluetoothDevice remoteDev = bluetoothAdapter.getRemoteDevice("00:1B:10:81:4F:CB");
        socket = remoteDev.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

        Log.i(TAG, "Connecting to remote device");
        bluetoothAdapter.cancelDiscovery();
        socket.connect();

        Log.i(TAG, "Connected");

        inStream = socket.getInputStream();
        outStream = socket.getOutputStream();
    }

    private void sendAndAssertReceive(String toSend, String expectedResponse) throws java.io.IOException{
        byte[] response = sendAndGetResponse(toSend, expectedResponse.length());

        for (int i = 0; i < response.length; i++) {
            if (expectedResponse.getBytes()[i] != response[i]) {
                Log.e(TAG, "Got unexpected result at place " + i + " " + response[i]);
                throw new RuntimeException("Got an unexpected response");
            }
        }
    }

    private byte[] sendAndGetResponse(String toSend, int expectedResponseLength) throws IOException {
        outStream.write(toSend.getBytes());
        byte[] response = new byte[expectedResponseLength];
        inStream.read(response);

        return response;
    }

    public void disarm() throws java.io.IOException {
        sendAndAssertReceive("0", "1");
        Log.i(TAG,"Disarmed!");
        state = false;
    }

    public void arm() throws java.io.IOException {
        sendAndAssertReceive("1", "1");
        Log.i(TAG,"Armed!");
        state = true;
    }

    public boolean isArmed() throws IOException
    {
        byte[] response = sendAndGetResponse("4", 1);
        return response[0] == '1';
    }
}
