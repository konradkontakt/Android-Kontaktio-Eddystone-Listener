package com.example.konradbujak.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private ProximityManager KontaktioManager;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private static final String TAG = "MyActivity";
    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<String> uids = new ArrayList<String>();
    MySimpleArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For devices with Android v6.0+ we need to ask for permission as it is required by Kontakt.io SDK
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                KontaktioStart();
                Log.i(TAG, "We have already permission" );
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startService(new Intent(getBaseContext(), Service.class));
                    Log.i(TAG, "Permission granted");
                    KontaktioStart();
                } else {
                    // permission denied, boo!
                    Log.i(TAG, "Permission denied");
                    Context context = getApplicationContext();
                    CharSequence text = "You have to grant permission in order to use Kontakt.io Beacon Health";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //startService(new Intent(getBaseContext(), Service.class));
                        Log.i(TAG, "Permission granted");
                        KontaktioStart();
                    }
                    return;
                }
            }
        }
    }
    public void KontaktioStart() {
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
                String url = eddystone.getUrl();
                urls.add(url);
                String uid = eddystone.getUniqueId();
                uids.add(uid);
                adapter.updateUrls(urls, uids);
                //Dumping urls to the logs
                Log.v(TAG,"URL :" + eddystone.getUrl() + " " + "Beacon UID : " + eddystone.getUniqueId());
            }
        });
        startScanning();
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
