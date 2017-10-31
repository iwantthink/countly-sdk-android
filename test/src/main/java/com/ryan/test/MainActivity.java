package com.ryan.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.DeviceId;

public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Countly.sharedInstance().init(this.getApplicationContext(), "https://asia-try.count.ly",
                "a3a3c83523bcfd981f684dd2988dca79703e0325", null,
                DeviceId.Type.OPEN_UDID);

        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();


    }

    @Override
    public void onStart() {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    public void onStop() {
        Countly.sharedInstance().onStop();
        super.onStop();
    }

    public void btn_action_click(View view) {
        Countly.sharedInstance().recordEvent("CLICK_ME_ASSHHOL", 1);
    }

    public void btn_seconedactivity(View view) {
        SeconedActivity.start(MainActivity.this);
    }
}
