package com.example.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON){
        HashMap<String,String> googlePlaceMap=new HashMap<>();
        String name = "",vicinity = "",latitude="",longitude="",reference,status,rating,type ="",photoRef = "";

        try {
            if(!googlePlaceJSON.isNull("name"))
                name=googlePlaceJSON.getString("name");
            if(!googlePlaceJSON.isNull("vicinity"))
                vicinity=googlePlaceJSON.getString("vicinity");
            latitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference=googlePlaceJSON.getString("reference");
//            status=googlePlaceJSON.getJSONObject("opening_hours").getString("open_now");
//            rating=googlePlaceJSON.getString("rating");
//            rating+="("+googlePlaceJSON.getString("user_ratings_total")+")";
//            JSONArray jsonArray=googlePlaceJSON.getJSONArray("types");
//            for(int i=0;i<1;i++)
//                type=jsonArray.getJSONObject(i).toString();
//            jsonArray=googlePlaceJSON.getJSONArray("photos");
//            for(int i=0;i<1;i++)
//                photoRef=jsonArray.getJSONObject(i).getString("photo_reference");

            googlePlaceMap.put("name",name);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("latitude",latitude);
            googlePlaceMap.put("longitude",longitude);
            googlePlaceMap.put("reference",reference);
//            googlePlaceMap.put("rating",rating);
//            googlePlaceMap.put("status",status);
//            googlePlaceMap.put("type",type);
//            googlePlaceMap.put("photoRef",photoRef);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  googlePlaceMap;
    }


    private List<HashMap<String,String >> getAllNearbyPlaces(JSONArray jsonArray){
        List<HashMap<String,String>> nearbyplaceslist=new ArrayList<>();
        HashMap<String,String> nearbyplacemap=null;
        for(int i=0;i<jsonArray.length();i++){
            try {
                nearbyplacemap=getSingleNearbyPlace((JSONObject)jsonArray.getJSONObject(i));
                nearbyplaceslist.add(nearbyplacemap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyplaceslist;
    }


    protected List<HashMap<String,String>> parse(String jsondata){
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsondata);
            jsonArray=jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);
    }
}
