package com.example.abhinav.memorableplaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String element="Add a New Place...";
    static ArrayList<String> places=new ArrayList<String>();

    static ArrayList<LatLng> location=new ArrayList<LatLng>();


    static ArrayList<String> latitudes=new ArrayList<String>();
    static ArrayList<String> longitudes=new ArrayList<String>();
    static ArrayAdapter adapter;
    static SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudes=new ArrayList<String>();
        longitudes=new ArrayList<String>();

        preferences=this.getSharedPreferences("com.example.abhinav.memorableplaces", Context.MODE_PRIVATE);


        if(preferences.getAll().size()>0) {

            try {
                places = (ArrayList<String>) ObjectSerializer.deserialize(preferences.getString("Places", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else{


            places.add(element);
        }



        try {

            latitudes=(ArrayList<String>) ObjectSerializer.deserialize(preferences.getString("Latitudes", ObjectSerializer.serialize(new ArrayList<String>())));

            longitudes=(ArrayList<String>) ObjectSerializer.deserialize(preferences.getString("Longitudes", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0;i<latitudes.size();i++){

           LatLng latLng=new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i)));

           location.add(latLng);


        }



        ListView placeList=(ListView)findViewById(R.id.myList);

        adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,places);

        placeList.setAdapter(adapter);

        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("view",places.get(i));

                if(i==0){



                    Intent in=new Intent(getApplicationContext(),MapsActivity.class);




                    startActivity(in);





                }

                else{
                    Log.i("1234","in here");

                    Intent in=new Intent(getApplicationContext(),MapsActivity.class);

                    in.putExtra("index",i);

                    startActivity(in);





                }

            }
        });


        placeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                final int index = i;

                if (index != 0)
                {


                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete this place")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    places.remove(index);
                                    latitudes.remove(index - 1);
                                    longitudes.remove(index - 1);
                                    location.remove(index - 1);
                                    preferences.edit().clear();

                                    adapter.notifyDataSetChanged();

                                    preferences.edit().clear();
                                    try {

                                        preferences.edit().putString("Places", ObjectSerializer.serialize(MainActivity.places)).apply();
                                        preferences.edit().putString("Latitudes", ObjectSerializer.serialize(latitudes)).apply();
                                        preferences.edit().putString("Longitudes", ObjectSerializer.serialize(longitudes)).apply();


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

            }
                return true;
            }
        });










    }
}
