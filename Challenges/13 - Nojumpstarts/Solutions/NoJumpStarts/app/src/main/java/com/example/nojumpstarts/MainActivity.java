package com.example.nojumpstarts;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Objects;

public class MainActivity extends Activity {

    private static final String TAG = "MOBIOTSEC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created the activity");
    }

    @Override
    protected void onStart() {
        super.onStart();

        String msg = "Main-to-A/A-to-B/B-to-C";

        // Use the buildIntent method from Main to create the intent
        Intent data = null;
        try {
            data = Main.buildIntent("Main", "C", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Log.i(TAG, "Signed the message and created intent");

        if (Objects.requireNonNull(data).resolveActivity(getPackageManager()) != null) {
            // We then set authmsg and authsign as extras
            data.putExtra("authmsg", msg);
            // We can then sign the message with the code already present inside "Main"
            // The following has to be try-catch surrounded
            try {
                data.putExtra("authsign", Main.sign(msg));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            Log.i(TAG, "Sent intent successfully");
            startActivityForResult(data, 1);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String flagValue = data.getStringExtra("flag");

            if (flagValue != null) {
                Log.i(TAG, flagValue);
            } else {
                Log.e(TAG, "Flag not present in the intent");
            }
        } else {
            Log.e(TAG, "Received null intent");
        }
    }
}
