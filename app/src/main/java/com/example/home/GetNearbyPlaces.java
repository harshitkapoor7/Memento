package com.example.home;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    private String googleplacedata,url;
    private GoogleMap mMap;
    public List<HashMap<String,String>> nearbyPlacesList=null;

    @Override
    protected String doInBackground(Object... objects) {
        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];

        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googleplacedata=downloadUrl.ReadURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplacedata;
    }


    @Override
    public void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList=null;
        DataParser dataParser=new DataParser();
        nearbyPlacesList=dataParser.parse(s);
        DisplayNearbyPlaces(nearbyPlacesList);
    }

    private void DisplayNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList){
        for(int i=0;i<nearbyPlacesList.size();i++){
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String> nearbyPlace=nearbyPlacesList.get(i);
            String name=nearbyPlace.get("name");
            String vicinity=nearbyPlace.get("vicinity");
            double lat=Double.parseDouble(nearbyPlace.get("latitude"));
            double lng=Double.parseDouble(nearbyPlace.get("longitude"));
            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(name+" : "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(markerOptions);
            if(i==0)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12f));
        }
    }
}
