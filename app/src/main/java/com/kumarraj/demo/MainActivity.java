package com.kumarraj.demo;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity /*implements View.OnClickListener */ {

    private Button start, stop;
    private TextView textView;
    private BroadcastReceiver gpsBroadcastReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        if (gpsBroadcastReceiver == null){
            gpsBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String coordinates = (String)intent.getExtras().get("coordinates");
                    Toast.makeText(MainActivity.this, coordinates, Toast.LENGTH_SHORT).show();
                    textView.append("\n" + coordinates);

                }
            };
        }
        registerReceiver(gpsBroadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsBroadcastReceiver != null){
            unregisterReceiver(gpsBroadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.btnStartService);
        stop = (Button) findViewById(R.id.btnStopService);
        textView = (TextView) findViewById(R.id.textView);

        if (!runtime_permission()){
            enableButtons();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Started", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, GpsService.class));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Stopped", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, GpsService.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enableButtons();
            }else{
                runtime_permission();
            }
        }
    }

    private void enableButtons() {

    }

    private boolean runtime_permission(){

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

}
