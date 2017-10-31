package ly.count.android.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import ly.count.android.sdk.Countly;


public class MainActivity extends Activity {
    private String demoTag = "CountlyDemo";
    private Activity activity;

    /** You should use try.count.ly instead of YOUR_SERVER for the line below if you are using Countly trial service */
    final String COUNTLY_SERVER_URL = "https://asia-try.count.ly";
    final String COUNTLY_APP_KEY = "a3a3c83523bcfd981f684dd2988dca79703e0325";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context appC = getApplicationContext();

        Countly.onCreate(this);
        Countly.sharedInstance().setLoggingEnabled(true);
        Countly.sharedInstance().enableCrashReporting();
        //Countly.sharedInstance().setHttpPostForced(true);
        //Log.i(demoTag, "Before calling init. This should return 'false', the value is:" + Countly.sharedInstance().isInitialized());
        Countly.sharedInstance().init(appC, COUNTLY_SERVER_URL, COUNTLY_APP_KEY);
        //Log.i(demoTag, "After calling init. This should return 'true', the value is:" + Countly.sharedInstance().isInitialized());

    }

    public void onClickButtonCustomEvents(View v) {
        startActivity(new Intent(this, ActivityExampleCustomEvents.class));
    }

    public void onClickButtonCrashReporting(View v) {
        startActivity(new Intent(this, ActivityExampleCrashReporting.class));
    }

    public void onClickButtonUserDetails(View v) {
        startActivity(new Intent(this, ActivityExampleUserDetails.class));
    }

    public void onClickButtonAPM(View v) {
        //
    }

    public void onClickButtonViewTracking(View v) {
        startActivity(new Intent(this, ActivityExampleViewTracking.class));
    }

    public void onClickButtonMultiThreading(View v) {
        //
    }

    public void onClickButtonOthers(View v) {
        startActivity(new Intent(this, ActivityExampleOthers.class));
    }


    public void enableCrashTracking(){
        //add some custom segments, like dependency library versions
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Facebook", "3.5");
        data.put("Admob", "6.5");
        Countly.sharedInstance().setCustomCrashSegments(data);
        Countly.sharedInstance().enableCrashReporting();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    public void onStop()
    {
        Countly.sharedInstance().onStop();
        super.onStop();
    }

}
