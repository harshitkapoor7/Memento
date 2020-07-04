package com.example.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class SplashActivity extends Activity {
    Handler handler;
    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        int flag=0;
        if (ConnChecker.check(getBaseContext()) == false) {
            Intent intent = new Intent(this, ConnectionChecker.class);
            flag=1;
            startActivity(intent);
            finish();
        }

        handler = new Handler();
        if(flag == 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 5000);
        }


    }

}

