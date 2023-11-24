package com.example.maliciousapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

public class HashMe extends Activity {

    private final static String TAG = "MOBIOTSEC";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();

        Log.i(TAG, "Dat: " + intent.getData().toString() );

        // calculate hash
        String hash = "";

        try {
            hash = calcHash(intent.getData().toString());
        } catch( Exception e ) {
            Log.e(TAG, "Exception: " + Log.getStackTraceString(e));
        }
        // return the hash in a "result" intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra("hash", hash);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    protected String calcHash(String uriString) throws NoSuchAlgorithmException {
        String hash = "";
        Uri uri = Uri.parse(uriString);

        try {
            InputStream in = getContentResolver().openInputStream(uri);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line);
            }

            hash = total.toString();
            Log.i(TAG, "Hash String: " + hash);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"InputStream Exception");
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] sha256 = md.digest(hash.getBytes(StandardCharsets.UTF_8));

        hash = Hex.toHexString(sha256);
        Log.i(TAG, "Hash SHA256: " + hash);

        return hash;
    }

}
