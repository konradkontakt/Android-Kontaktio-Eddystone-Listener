package com.example.konradbujak.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private ProximityManager KontaktioManager;
    private static final String TAG = "MyActivity";
    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<String> uids = new ArrayList<String>();
    MySimpleArrayAdapter adapter;
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
        //Dumping urls to the logs
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener()
        {
            @Override public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace)
            {
                String url = eddystone.getUrl();
                urls.add(url);
                String uid = eddystone.getUniqueId();
                uids.add(uid);
                Log.v(TAG,"URL :" + eddystone.getUrl() + " " + "Beacon UID : " + eddystone.getUniqueId());
                //Url is first line, second lane is UID
            }
            });
        startScanning();
        ListView lista = (ListView) findViewById(R.id.CustomListView);
        adapter = new MySimpleArrayAdapter(this, urls, uids);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(arg2)));
                startActivity(browserIntent);
            }
        });
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
