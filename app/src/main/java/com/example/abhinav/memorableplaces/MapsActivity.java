package com.example.abhinav.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.io.Serializable;

import static com.example.abhinav.memorableplaces.MainActivity.places;
import static com.example.abhinav.memorableplaces.MainActivity.preferences;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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





        if (Build.VERSION.SDK_INT < 23) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }

        else{
            if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){


                ActivityCompat.requestPermissions(this,new String [] {Manifest.permission.ACCESS_FINE_LOCATION},1);

            }
            else{

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }


        }




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

        LatLng loc;

        Log.i("index",""+getIntent().getIntExtra("index",0));



        if(getIntent().hasExtra("index")){

            int index=getIntent().getIntExtra("index",0);






            loc=new LatLng(Double.parseDouble(MainActivity.latitudes.get(index-1)),Double.parseDouble(MainActivity.longitudes.get(index-1)));



        }
        else {
            loc = new LatLng(28.6287564, 77.0691296);


        }

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addressList=null;

        try {
            addressList=geocoder.getFromLocation(loc.latitude,loc.longitude,1);


        } catch (IOException e) {
            e.printStackTrace();
        }


        mMap.addMarker(new MarkerOptions().position(loc).title(addressList.get(0).getAddressLine(0)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,6));




        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.i("here0",latLng.toString());




                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList=null;

                try {
                    addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);


                } catch (IOException e) {
                    e.printStackTrace();
                }




                if(addressList!=null &&addressList.size()>0 ) {

                    mMap.clear();

                    mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getAddressLine(0)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }




            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList = null;

                try {
                    addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

                    Log.i("Address taken",addressList.toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(addressList!=null &&addressList.size()>0 ) {

                    mMap.clear();

                    mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getAddressLine(0)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }


                MainActivity.places.add(addressList.get(0).getAddressLine(0));

                MainActivity.adapter.notifyDataSetChanged();

                MainActivity.location.add(latLng);



                MainActivity.preferences.edit().clear();



                try {

                    MainActivity.latitudes.clear();
                    MainActivity.longitudes.clear();



                    for(LatLng coordinates:MainActivity.location){


                        MainActivity.latitudes.add(Double.toString(coordinates.latitude));
                        MainActivity.longitudes.add(Double.toString(coordinates.longitude));
                    }


                    MainActivity.preferences.edit().putString("Places",ObjectSerializer.serialize(MainActivity.places)).apply();
                    MainActivity.preferences.edit().putString("Latitudes",ObjectSerializer.serialize(MainActivity.latitudes)).apply();
                    MainActivity.preferences.edit().putString("Longitudes",ObjectSerializer.serialize(MainActivity.longitudes)).apply();


                } catch (IOException e) {
                    e.printStackTrace();
                }

               if(MainActivity.preferences.contains("Location"))
                MainActivity.preferences.edit().remove("Location");




                Toast.makeText(getApplicationContext(),"Location saved",Toast.LENGTH_SHORT).show();
                Log.i("Toast","in here");










            }
        });
    }
}
