/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.openlocate.android.config.Configuration;
import com.openlocate.android.core.OpenLocate;
import com.openlocate.android.exceptions.InvalidConfigurationException;
import com.openlocate.android.exceptions.LocationConfigurationException;
import com.openlocate.android.exceptions.LocationPermissionException;
import com.openlocate.android.exceptions.LocationServiceConflictException;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    static final int LOCATION_PERMISSION = 1001;

    private static String TAG = MainActivity.class.getSimpleName();

    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });

        OpenLocate openLocate = OpenLocate.getInstance(getApplicationContext());
        if (openLocate.isTracking()) {
            onStartService();
        }
    }

    private void startTracking() {

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + BuildConfig.TOKEN);
            Configuration configuration = new Configuration.ConfigurationBuilder()
                    .setUrl(BuildConfig.URL)
                    .setHeaders(headers)
                    .createConfiguration();
            OpenLocate openLocate = OpenLocate.getInstance(getApplicationContext());
            openLocate.startTracking(configuration);
            Toast.makeText(this, "Location service started", Toast.LENGTH_LONG).show();
            onStartService();
        } catch (InvalidConfigurationException | LocationServiceConflictException | LocationConfigurationException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        } catch (LocationPermissionException e) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION
            );
        }
    }

    private void stopTracking() {
        OpenLocate.getInstance(getApplicationContext()).stopTracking();
        Toast.makeText(this, "Location service stopped", Toast.LENGTH_LONG).show();
        onStopService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                onLocationRequestResult(grantResults);
                break;
        }
    }

    private void onLocationRequestResult(@NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        }
    }

    private void onStartService() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void onStopService() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}
