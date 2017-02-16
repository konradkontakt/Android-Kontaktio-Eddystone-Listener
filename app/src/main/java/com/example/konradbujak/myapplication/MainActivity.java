package com.example.konradbujak.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.ScanStatusListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleScanStatusListener;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private ProximityManager KontaktioManager;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private static final String TAG = "XYZ";
    //Replace (Your Secret API key) with your API key aquierd from the Kontakt.io Web Panel
    public static String API_KEY = "Your Secret API key";
    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<String> uids = new ArrayList<String>();
    MySimpleArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        onetimeconfiguration();
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        checkPermissionAndStart();
    }
    @Override
    protected void onStop() {
        KontaktioManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        KontaktioManager.disconnect();
        KontaktioManager = null;
        super.onDestroy();
    }
    // Toasts on device
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    // Checking the permissions for the Android OS 6.0 +
    private void checkPermissionAndStart() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Arrays.toString(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}));
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermissionResult) {
            //already granted
            Log.d(TAG,"Permission already granted");
            startScan();
            adapter();
        }
        else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            Log.d(TAG,"Permission request called");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (100 == requestCode) {
                Log.d(TAG,"Permission granted");
                startScan();
                adapter();

            }
            showToast("Kontakt.io SDK require this permission");
        } else
        {
            Log.i(TAG, "Permission denied");
            //Display toast
            Context context = getApplicationContext();
            CharSequence text = "You have to grant permission in order to use Kontakt.io Beacon Health";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            // I will give them one more time option after 3 seconds
            final CountDownTimer counter1 = new CountDownTimer(3000, 1)
            {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    Log.d(TAG, "Countdown Finished");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "Permission granted");
                        startScan();
                        adapter();
                    }
                }
            };
            counter1.start();
        }
    }
    private void adapter()
    {
        ListView lista = (ListView) findViewById(R.id.CustomListView);
        adapter = new MySimpleArrayAdapter(this);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(arg2)));
                startActivity(browserIntent);
            }
        });
    }
    private void onetimeconfiguration(){
        sdkInitialise();
        configureProximityManager();
        setListeners();
    }
    public void sdkInitialise()
    {
        KontaktSDK.initialize(API_KEY);
        if (KontaktSDK.isInitialized())
            Log.v(TAG, "SDK initialised");
    }
    private void configureProximityManager() {
        KontaktioManager = ProximityManagerFactory.create(this);
        KontaktioManager.configuration()
                .rssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(5))
                .resolveShuffledInterval(3)
                .scanMode(ScanMode.BALANCED)
                .eddystoneFrameTypes(EnumSet.of(EddystoneFrameType.URL))
                .scanPeriod(ScanPeriod.create(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(5)))
                .activityCheckConfiguration(ActivityCheckConfiguration.DEFAULT);
    }
    public void setListeners() {
        KontaktioManager.setScanStatusListener(createScanStatusListener());
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener()
        {
            @Override public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace)
            {
                String url = eddystone.getUrl();
                urls.add(url);
                String uid = eddystone.getUniqueId();
                uids.add(uid);
                adapter.updateUrls(urls, uids);
                //Dumping urls to the logs
                Log.v(TAG,"URL :" + eddystone.getUrl() + " " + "Beacon UID : " + eddystone.getUniqueId());
            }
        });
    }
    private ScanStatusListener createScanStatusListener() {
        return new SimpleScanStatusListener() {
            @Override
            public void onScanStart()
            {
                Log.d(TAG,"Scanning started");
                showToast("Scanning started");

            }
            @Override
            public void onScanStop()
            {
                Log.d(TAG,"Scanning stopped");
                showToast("Scanning stopped");
            }
        };
    }
    private void startScan() {
        KontaktioManager.connect(new OnServiceReadyListener()
        {
            @Override
            public void onServiceReady() {
                KontaktioManager.startScanning();

            }
        });
    }
}
