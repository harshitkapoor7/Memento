package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.List;

public class NearbyItems extends AppCompatActivity {
    RecyclerView recyclerView;
    List<HashMap<String,String>> list;
    public NearbyItems(List<HashMap<String,String>> list){
        this.list=list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_items);
        Intent i=getIntent();
        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ListItemsAdapter(list));

    }

    public void showRecyclerView(){

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListItemsAdapter(list));
    }

}
