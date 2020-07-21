package com.example.wifisimple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // UI components
    ListView lv;
    Button buttonScan;

    // variables
    int size = 0;

    // objects
    WifiManager wifi;
    ArrayAdapter adapter;
    List<ScanResult> results;
    ArrayList<String> arraylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize UI components
        buttonScan = findViewById(R.id.scan);
        buttonScan.setOnClickListener(this);
        lv = findViewById(R.id.wifilist);

        // get default Wifi service
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // enable Wifi
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        // initialize ArrayAdapter object
        this.adapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arraylist);
        // set adapter to list
        lv.setAdapter(this.adapter);
    }

    public void onClick(View view)
    {
        scanWifiNetworks();
    }

    private void scanWifiNetworks(){
        arraylist.clear();
        //register broadcast listener
        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // request a scan
        wifi.startScan();

        Log.d("WifScanner", "scanWifiNetworks");
        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifi_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d("WifScanner", "onReceive");

            // get scan results
            results = wifi.getScanResults();
            size = results.size();

            // set items into adapter
            try
            {
                while (size >= 0)
                {
                    size--;
                    arraylist.add(results.get(size).SSID);
                    adapter.notifyDataSetChanged();
                }
            }
            catch (Exception e)
            {
                Log.w("WifScanner", "Exception: "+e);
            }

            // unregister receiver
            unregisterReceiver(this);
        }

    };
}