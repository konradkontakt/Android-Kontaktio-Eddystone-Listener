package com.example.konradbujak.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private ProximityManager KontaktioManager;
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KontaktSDK.initialize("QcZNRdfovwLcPVFAvbHgacOnfGBkcHco");
        if (KontaktSDK.isInitialized())
            Log.v(TAG, "SDK initialised");
        KontaktioManager = new ProximityManager(this);
        KontaktioManager.configuration().eddystoneFrameTypes(Collections.singleton(EddystoneFrameType.URL))
                .scanMode(ScanMode.BALANCED)
                .scanPeriod(ScanPeriod.RANGING);
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener()
        {
            @Override public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace)
            {
                Log.v(TAG,"URL :" + eddystone.getUrl() + " " + "Beacon UID : " + eddystone.getUniqueId());
                //Url is first line, second lane is UID
            }
            });
        startScanning();
    }
    private void startScanning(){
        KontaktioManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                KontaktioManager.startScanning();
                if (KontaktioManager.isScanning())
                    Log.i(TAG, "Scan started");
            }
        });
    }
}
