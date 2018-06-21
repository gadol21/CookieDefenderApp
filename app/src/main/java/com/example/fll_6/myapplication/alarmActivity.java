package com.example.fll_6.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class alarmActivity extends AppCompatActivity {

    private CookieCommunicator cookie;
    private Button btn;
    private boolean isConnected;
    private int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "alarmAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new RuntimeException("BT not supported!");
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            finishGui();
        }
    }

    private void finishGui() {
        try {
            cookie = new CookieCommunicator(new Runnable() {
                @Override
                public void run(){
                    Log.i(TAG, "Unauthorized access!!");
                }
            });
            isConnected = true;
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            isConnected = false;
        }

        btn =  findViewById(R.id.Button);

        if(isConnected) {
            try{
                if(cookie.isArmed())
                {
                    makeButtonDisarm(btn);
                }else{
                    makeButtonArm(btn);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (cookie.isArmed()) {
                            cookie.disarm();
                            makeButtonArm(btn);
                        } else {
                            cookie.arm();
                            makeButtonDisarm(btn);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            btn.setEnabled(false);
            btn.setText("NotConnected");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Finished turning on bt");
                finishGui();
            } else {
                Log.e(TAG, "Failed turning on bt");
                Toast.makeText(getApplicationContext(), "You can't control jar without BT", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void makeButtonDisarm(Button btn)
    {
        btn.setBackgroundColor(getResources().getColor(R.color.green));
        btn.setText("Disarm");
    }

    public void makeButtonArm(Button btn)
    {
        btn.setBackgroundColor(getResources().getColor(R.color.red));
        btn.setText("Arm");
    }
}