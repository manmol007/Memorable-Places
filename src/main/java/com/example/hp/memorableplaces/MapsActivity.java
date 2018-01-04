package com.example.hp.memorableplaces;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.internal.e;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                   try {
                       Location xyz = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                       zoomIn(xyz, "your location");
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                   }
            }
        }


    }

    public void zoomIn(Location location, String s){

        try{
            LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(s != "your location")
            mMap.addMarker(new MarkerOptions().position(userlocation).title(s).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 10));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        Intent intent=getIntent();


        if(intent.getIntExtra("placenumber",0) ==0){

            //zoom on user location

            locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    try {
                        zoomIn(location, "your location");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if(Build.VERSION.SDK_INT<23){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
            else {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    try{
                    Location xyz=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    zoomIn(xyz,"your location");}
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
            }

        }
        else{

            try {
                Location placelocation = new Location(LocationManager.GPS_PROVIDER);
                placelocation.setLatitude(MainActivity.location.get(intent.getIntExtra("placenumber", 0)).latitude);
                placelocation.setLongitude(MainActivity.location.get(intent.getIntExtra("placenumber", 0)).longitude);
                zoomIn(placelocation, MainActivity.arr.get(intent.getIntExtra("placenumber", 0)));


            }catch (Exception e){
                e.printStackTrace();
            }
            }
        }




    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder=new Geocoder(this, Locale.getDefault());

        String address="";

        try {
            List<android.location.Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList !=null && addressList.size()>0){

                if(addressList.get(0).getSubThoroughfare() != null){
                    address+=addressList.get(0).getSubThoroughfare() +",";
                }

                if(addressList.get(0).getThoroughfare() != null){
                    address+=addressList.get(0).getThoroughfare() +",";
                }
                if(addressList.get(0).getCountryName() !=null){

                        address+=addressList.get(0).getCountryName() +"-";
                    }
                if (addressList.get(0).getPostalCode() !=null){

                    if(addressList.get(0).getSubThoroughfare() != null){
                        address+=addressList.get(0).getPostalCode();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(address ==""){

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:HH yyyy-MM-dd");
            address=simpleDateFormat.format(new Date());

        }

        ArrayList<String> lattitute=new ArrayList<>();
        ArrayList<String> longitute=new ArrayList<>();

        for(LatLng location:MainActivity.location){

            lattitute.add(Double.toString(location.latitude));
            longitute.add(Double.toString(location.longitude));
        }

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.hp.memorableplaces",Context.MODE_PRIVATE);

        try {
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.arr)).apply();
            sharedPreferences.edit().putString("latitute",ObjectSerializer.serialize(lattitute)).apply();
            sharedPreferences.edit().putString("longitute",ObjectSerializer.serialize(longitute)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            MainActivity.arr.add(address);
        try {
            MainActivity.location.add(latLng);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        MainActivity.arrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"Location saved",Toast.LENGTH_LONG);
    }
}