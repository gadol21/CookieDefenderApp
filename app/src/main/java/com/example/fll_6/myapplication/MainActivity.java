package com.example.fll_6.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i = new Intent(this,newActivity.class);
        splashScreen(3000);
    }

    public void splashScreen(final int x)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(x);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                startActivity(i);
                finish();
            }
        }).start();
    }
}
