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

import java.io.IOException;
import java.util.Set;

public class alarmActivity extends AppCompatActivity {

    private CookieCommunicator cookie;
    private Button btn;
    private boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        try {
            cookie = new CookieCommunicator();
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
