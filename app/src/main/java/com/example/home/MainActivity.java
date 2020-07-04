package com.example.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton button;
    private ConstraintLayout cl;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AutoCompleteTextView search;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private ProgressBar progressBar;
    LocationManager lm;
    private ImageView cancel;
    private Double lat, lng;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getLocationPermission();

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        search = findViewById(R.id.searchBar);
        search.setDropDownBackgroundResource(R.color.autocomplete_bgc);
        search.setAdapter(new PlaceAutoSuggestAdapter(MainActivity.this, android.R.layout.simple_list_item_1));
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.gps);
        cancel = findViewById(R.id.cancel);





        lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        turnGpsOnAndFindLocation(0);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ConnChecker.check(getBaseContext()) == false) {

                    Intent intent=new Intent(view.getContext(),ConnectionChecker.class);
//                    getSupportActionBar().show();
//                    getActionBar().show();
                    startActivity(intent);
                    finish();
                }
                search.setText("");
                turnGpsOnAndFindLocation(0);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancel.getVisibility() == View.VISIBLE)
                    search.setText("");
            }
        });


    }

    public void turnGpsOnAndFindLocation(final int flag) {
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "Turn on GPS", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Fetching Your Location", Toast.LENGTH_LONG).show();
            new GpsUtils(MainActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {

                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        getDeviceLocation(flag);
                        progressBar.setVisibility(View.GONE);
                        cancel.setVisibility(View.VISIBLE);
                    }
                }
            }, 11000);
        } else {
            getDeviceLocation(flag);
            progressBar.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private String getURL(double lat, double lng, String placetype) {
        StringBuilder link = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=");
        link.append(lat + "," + lng);
        link.append("&radius=5500&type=" + placetype);
        link.append("&key=AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k");
        return link.toString();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (ConnChecker.check(getBaseContext()) == false) {

            Intent intent=new Intent(getApplicationContext(),ConnectionChecker.class);
//                    getSupportActionBar().show();
//                    getActionBar().show();
            startActivity(intent);
            finish();
        }
        else {
            int id = item.getItemId();
            Object transferData[] = new Object[2];
            transferData[0] = mMap;
            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
            String url = "";
//        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog();
//        LayoutInflater inflater= (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.bottom_sheet,null);
//        recyclerView=findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


            switch (id) {
                case R.id.restaurant:
                    mMap.clear();
                    url = getURL(lat, lng, "restaurant");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
//                List<HashMap<String,String>> list=getNearbyPlaces.nearbyPlacesList;
//                NearbyItems nearbyItems =new NearbyItems(list);
//                nearbyItems.showRecyclerView();
//                Intent i =new Intent(this,NearbyItems.class);
//                startActivity(i);

//                recyclerView.setAdapter(new ListItemsAdapter(getApplicationContext(),list));

//                bottomSheetDialog.show(getSupportFragmentManager(),"abc");
                    break;
                case R.id.hospital:
                    mMap.clear();
                    url = getURL(lat, lng, "hospital");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    List<HashMap<String, String>> list1 = getNearbyPlaces.nearbyPlacesList;
//                recyclerView.setAdapter(new ListItemsAdapter(list1));
//                bottomSheetDialog.show(getSupportFragmentManager(),"def");
                    break;
                case R.id.malls:
                    mMap.clear();
                    url = getURL(lat, lng, "shopping_mall");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
//                List<HashMap<String,String>> list2=getNearbyPlaces.nearbyPlacesList;
//                recyclerView.setAdapter(new ListItemsAdapter(list2));
//                bottomSheetDialog.show(getSupportFragmentManager(),"ghi");
                    break;
                case R.id.hotel:
                    mMap.clear();
                    url = getURL(lat, lng, "lodging");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
//                List<HashMap<String,String>> list3=getNearbyPlaces.nearbyPlacesList;
//                recyclerView.setAdapter(new ListItemsAdapter(list3));
//                bottomSheetDialog.show(getSupportFragmentManager(),"jkl");
                    break;
                case R.id.atm:
                    mMap.clear();
                    url = getURL(lat, lng, "atm");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    List<HashMap<String, String>> list4 = getNearbyPlaces.nearbyPlacesList;
//                recyclerView.setAdapter(new ListItemsAdapter(list4));
//                bottomSheetDialog.show(getSupportFragmentManager(),"mno");
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);


            search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mMap.clear();
                    LatLng latLng = getLatLngFromAddress(search.getText().toString());
                    hideKeyboard(MainActivity.this);
                    moveCamera(latLng, DEFAULT_ZOOM, search.getText().toString());

                }
            });

        }

    }

    public LatLng getLatLngFromAddress(String address) {

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void getDeviceLocation(final int flag) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            assert currentLocation != null;
                            if (flag == 0)
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "You're here");
                            lat = currentLocation.getLatitude();
                            lng = currentLocation.getLongitude();
                            System.out.println("main act" + lat + lng);


                        } else {
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
        System.out.println("main act" + lat + lng);
        System.out.println("main ac");
    }


    private void moveCamera(LatLng latLng, float zoom, String head) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (head != "You're here") {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(head);
            mMap.addMarker(markerOptions);
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                mapFragment.getMapAsync(MainActivity.this);
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        }
    }


}