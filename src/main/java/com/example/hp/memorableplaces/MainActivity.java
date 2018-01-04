package com.example.hp.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   static ArrayList<String> arr=new ArrayList<String>();

    static ArrayList<LatLng> location=new ArrayList<LatLng>();

    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list=(ListView)findViewById(R.id.list);

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.hp.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitute=new ArrayList<>();

        ArrayList<String> longitute=new ArrayList<>();

        arr.clear();
        latitute.clear();
        longitute.clear();
        location.clear();

        try {

            latitute=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitute",ObjectSerializer.serialize(new ArrayList<String>())));
            longitute=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longitute",ObjectSerializer.serialize(new ArrayList<String>()))) ;
            arr= (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));

        if(arr.size()>0 &&latitute.size()>0 && longitute.size()>0){
            if(arr.size()==latitute.size() && arr.size() == longitute.size()){
                for(int i=0;i<latitute.size();++i){
                    location.add(new LatLng(Double.parseDouble(latitute.get(i)),Double.parseDouble(latitute.get(i))));
                }
            }
        }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        arr.add("Add a new place");

        location.add(new LatLng(0,0));

         arrayAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arr);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placenumber",i);
                startActivity(intent);

            }
        });
    }
}
