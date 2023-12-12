package com.example.frontdoor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FlagCaller extends Activity {
    private static final String TAG = "MOBIOTSEC";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = (Future<String>) executorService.submit(new WebService());

        try {
            String response = future.get();
            Log.i(TAG, response);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            executorService.shutdown();
        }
    }

    private class WebService implements Runnable {

        @Override
        public void run() {
            try {
                URL mUrl = new URL("http://10.0.2.2:8085");
                String urlParameters = "username=testuser&password=passtestuser123";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                HttpURLConnection conn = (HttpURLConnection) new URL(mUrl + "?" + urlParameters).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postData);

                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                inputStream.close();
                conn.disconnect();

                Log.i(TAG, response.toString());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }
}
