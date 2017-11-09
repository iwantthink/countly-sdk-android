package ly.count.android.demo.messaging;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.messaging.CountlyMessaging;
import ly.count.android.sdk.messaging.Message;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends Activity {

    private BroadcastReceiver messageReceiver;

    /** You should use try.count.ly instead of YOUR_SERVER for the line below if you are using Countly trial service */
    final String COUNTLY_SERVER_URL = "https://asia-try.count.ly";
    final String COUNTLY_APP_KEY = "a3a3c83523bcfd981f684dd2988dca79703e0325";
    final String COUNTLY_MESSAGING_PROJECT_ID = "640228892478";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Countly.sharedInstance()
                .init(this, COUNTLY_SERVER_URL, COUNTLY_APP_KEY)
                .initMessaging(this, MainActivity.class, COUNTLY_MESSAGING_PROJECT_ID, Countly.CountlyMessagingMode.TEST)
//                .setLocation(LATITUDE, LONGITUDE);
                .setLoggingEnabled(true);

        Countly.sharedInstance().recordEvent("test", 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Countly.sharedInstance().recordEvent("test2", 1, 2);
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Countly.sharedInstance().recordEvent("test3");
            }
        }, 10000);

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

    @Override
    protected void onResume() {
        super.onResume();

        /** Register for broadcast action if you need to be notified when Countly message received */
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = intent.getParcelableExtra(CountlyMessaging.BROADCAST_RECEIVER_ACTION_MESSAGE);
                Log.i("CountlyActivity", "Got a message with data: " + message.getData());

                //Badge related things
                Bundle data = message.getData();
                String badgeString = data.getString("badge");
                try {
                    if(badgeString != null) {
                        int badgeCount = Integer.parseInt(badgeString);

                        boolean succeded = ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                        if (!succeded) {
                            Toast.makeText(getApplicationContext(), "Unable to put badge", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NumberFormatException exception) {
                    Toast.makeText(getApplicationContext(), "Unable to parse given badge number", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(CountlyMessaging.getBroadcastAction(getApplicationContext()));
        registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }
}
