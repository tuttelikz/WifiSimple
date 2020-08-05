package com.example.wifisimple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // constants
    private static final int PERMISSION_CODE = 1000;

    // variables
    int scanResultsSize = 0;

    // UI components
    ListView listWifi;
    Button buttonScan;

    // objects
    WifiManager wifiManager;
    List<ScanResult> scanResults;
    private final ArrayList<String> arraylist = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind UI components
        buttonScan = (Button) findViewById(R.id.scanButton);
        listWifi = (ListView)findViewById(R.id.wifiList);
        buttonScan.setOnClickListener(this);

        // check for permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            startWifiManager();
        }

        // set ArrayAdapter
        this.adapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arraylist);
        listWifi.setAdapter(this.adapter);
    }

    // handle runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                startWifiManager();
            } else {
                Toast.makeText(this, "Permission denied. Cannot run app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // handle default WifiManager
    public void startWifiManager() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        scanWifiNetworks();
    }

    @Override
    public void onClick(View view) {
        scanWifiNetworks();
    }

    // register BoaadcastReceiver
    private void scanWifiNetworks(){
        arraylist.clear();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiReceiver, intentFilter);

        wifiManager.startScan();
        Log.d("WifScanner", "Scanning Wifi Networks");
        Toast.makeText(this, "Scanning started...", Toast.LENGTH_SHORT).show();
    }

    // BroadcastReceiver class
    private final BroadcastReceiver wifiReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            scanResults = wifiManager.getScanResults();
            scanResultsSize = scanResults.size();
            unregisterReceiver(this);

            try {
                while (scanResultsSize >= 0) {
                    scanResultsSize--;
                    arraylist.add(scanResults.get(scanResultsSize).SSID);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}