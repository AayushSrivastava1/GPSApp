package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    Geocoder geocoder;
    double Thelongitude;
    double Thelatitude;
    ArrayList<Address> addressList;
    ArrayList<Location> thelocations;
    TextView displayLongitude;
    TextView displayLatitude;
    TextView displayaddress;
    TextView displaydistance;
    TextView timeDisplayer;
    ArrayList<Long> theTimeList;
    Button rb;
    double d;
    int time;
    int mr;
    Location temp1;
    Location temp2;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayaddress = findViewById(R.id.streetaddressdisplayer);
        displaydistance = findViewById(R.id.distancedisplayer);
        timeDisplayer = findViewById(R.id.timedisplayer);
        displayLongitude = findViewById(R.id.longitudedisplayer);
        displayLatitude = findViewById(R.id.latitudedisplayer);
        rb = findViewById(R.id.resetButton);
        theTimeList = new ArrayList<>();
        addressList = new ArrayList<>();
        thelocations = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        d = 0.0;
        time = 0;
        mr = 100;
        rb.setOnClickListener(v -> {
            d = 0.0;
            time = 0;
            timeDisplayer.setText("The Time(Seconds): " + time + " ");
            displaydistance.setText("The Distance(Miles): " + d + " ");
        });
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Thelongitude = location.getLongitude();
                Thelatitude = location.getLatitude();
                displayLongitude.setText("The Longitude: " + Double.toString(Thelongitude));
                displayLatitude.setText("The Latitude: " + Double.toString(Thelatitude));
                thelocations.add(location);
                geocoder = new Geocoder(getBaseContext(), Locale.US);
                try {
                    addressList.add(geocoder.getFromLocation(Thelatitude, Thelongitude, 1).get(0));
                    displayaddress.setText("The Address: "+addressList.get(addressList.size()-1).getAddressLine(0)+"");
                    if (addressList.size() > 1)
                    {
                        Location last = new Location("last location");
                        last.setLongitude(addressList.get(addressList.size() - 2).getLongitude());
                        last.setLatitude(addressList.get(addressList.size() - 2).getLatitude());
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (thelocations.size() > 1)
                {
                    temp1 = thelocations.get(thelocations.size() - 2);
                    temp2 = thelocations.get(thelocations.size() - 1);
                    d +=  (temp1.distanceTo(temp2)/ 1000) * 0.621;
                    d = rounderMethod(d);
                    time += (int) ((temp1.getTime() - temp2.getTime()) * -1) / 1000;
                }
                timeDisplayer.setText("The Time(Seconds): " + time + " ");
                displaydistance.setText("The Distance(Miles): " + d + " ");
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
                } catch (SecurityException e) {
                }
            }
        }
    }
        public double rounderMethod(double x)
        {
            String j = String.valueOf(x);
            String k = "";
            for (int i = 0; i < 5; i++)
            {
                k += j.charAt(i);
            }
            x = Double.parseDouble(k);
            return x;
        }
    }

