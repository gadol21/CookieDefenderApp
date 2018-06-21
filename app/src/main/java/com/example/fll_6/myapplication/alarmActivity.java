package com.example.fll_6.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
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
    private boolean isNotifSent;
    private int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "alarmAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isNotifSent = false;
        super.onCreate(savedInstanceState);
        createNotificationChannel();
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CookieChannel", "CookieChannel", importance);
            channel.setDescription("Cookie channel");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void popNotification(String text) {
        Intent viewIntent = new Intent(this, alarmActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(text)
                .setSmallIcon(R.drawable.ic_menu_send)
                //.setContentIntent(notificationPendingIntent)
                .setContentText(text)
                //.setContentIntent(viewPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("CookieChannel");
        }
        notificationManager.notify(55, builder.build());
    }

    private void finishGui() {
        try {
            cookie = new CookieCommunicator(new Runnable() {
                @Override
                public void run(){
                    if (!isNotifSent) {
                        isNotifSent = true;
                        popNotification("Unauthorized access");
                    }
                }
            });
            isConnected = true;
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            isConnected = false;
        }

        btn = (Button)findViewById(R.id.Button);

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
                            isNotifSent = false;
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