package com.example.alihan.loodosjobapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


public class SplashScreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String SPLASH_TEXT_KEY = "splash_text";
    TextView splashText;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashText = findViewById(R.id.splashText);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!checkConnection()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("You will need an internet connection to use this application");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();         //Firebase implementations
        mFirebaseRemoteConfig.fetch(0);
        mFirebaseRemoteConfig.activateFetched();
        splashText.setText(mFirebaseRemoteConfig.getString(SPLASH_TEXT_KEY));
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SUCCESS, "Splash Login");   //firebase logs
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

        Thread myThread = new Thread() {
            @Override
            public void run() {
                if (checkConnection()) {
                    try {
                        sleep(3000);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        myThread.start();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }
}
