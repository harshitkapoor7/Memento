package com.example.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.notifyme.NotificationPublisher;
import com.allyants.notifyme.NotifyMe;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        BottomSheetDialog.BottomSheetListener,BottomSheetNearby.BottomSheetNearbyListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

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
    private ProgressBar pb;
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
        button = findViewById(R.id.gps);
        cancel = findViewById(R.id.cancel);


        pb = findViewById(R.id.centerPrgBar);


        lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        turnGpsOnAndFindLocation(0);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ConnChecker.check(getBaseContext()) == false) {

                    Intent intent = new Intent(view.getContext(), ConnectionChecker.class);
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(marker.getTitle());
                bottomSheetDialog.show(getSupportFragmentManager(), "bottom");
                return false;
            }
        });

    }


    public void turnGpsOnAndFindLocation(final int flag) {
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "Turn on GPS", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.VISIBLE);
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
                        pb.setVisibility(View.GONE);
                        cancel.setVisibility(View.VISIBLE);
                    }
                }
            }, 11000);
        } else {
            getDeviceLocation(flag);
            pb.setVisibility(View.GONE);
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

            Intent intent = new Intent(getApplicationContext(), ConnectionChecker.class);
//                    getSupportActionBar().show();
//                    getActionBar().show();
            startActivity(intent);
            finish();
        } else {
            int id = item.getItemId();
            Object transferData[] = new Object[2];
            transferData[0] = mMap;
            final GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
            String url = "";
            final List<HashMap<String, String>>[] list = new List[1];
            Handler handler=new Handler();

            switch (id) {
                case R.id.restaurant:
                    mMap.clear();
                    pb.setBackgroundColor(getResources().getColor(R.color.black));
                    pb.setVisibility(View.VISIBLE);
                    url = getURL(lat, lng, "restaurant");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list[0] =getNearbyPlaces.nearbyPlacesList;
                            System.out.println(list[0] +"=list");
                            BottomSheetNearby bottomSheetNearby=new BottomSheetNearby(list[0],"res");
                            bottomSheetNearby.show(getSupportFragmentManager(), "bottomNearby");
                            pb.setVisibility(View.INVISIBLE);
                            pb.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    },1500);
                    break;

                case R.id.hospital:
                    mMap.clear();
                    pb.setBackgroundColor(getResources().getColor(R.color.black));
                    pb.setVisibility(View.VISIBLE);
                    url = getURL(lat, lng, "hospital");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    System.out.println(url);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list[0] =getNearbyPlaces.nearbyPlacesList;
                            System.out.println(list[0] +"=list");
                            BottomSheetNearby bottomSheetNearby=new BottomSheetNearby(list[0],"hos");
                            bottomSheetNearby.show(getSupportFragmentManager(), "bottomNearby");
                            pb.setVisibility(View.INVISIBLE);
                            pb.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    },1500);
                    break;

                case R.id.malls:
                    mMap.clear();
                    pb.setBackgroundColor(getResources().getColor(R.color.black));
                    pb.setVisibility(View.VISIBLE);
                    url = getURL(lat, lng, "shopping_mall");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list[0] =getNearbyPlaces.nearbyPlacesList;
                            System.out.println(list[0] +"=list");
                            BottomSheetNearby bottomSheetNearby=new BottomSheetNearby(list[0],"malls");
                            bottomSheetNearby.show(getSupportFragmentManager(), "bottomNearby");
                            pb.setVisibility(View.INVISIBLE);
                            pb.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    },1500);
                    break;

                case R.id.hotel:
                    mMap.clear();
                    pb.setBackgroundColor(getResources().getColor(R.color.black));
                    pb.setVisibility(View.VISIBLE);
                    url = getURL(lat, lng, "lodging");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list[0] =getNearbyPlaces.nearbyPlacesList;
                            System.out.println(list[0] +"=list");
                            BottomSheetNearby bottomSheetNearby=new BottomSheetNearby(list[0],"hotel");
                            bottomSheetNearby.show(getSupportFragmentManager(), "bottomNearby");
                            pb.setVisibility(View.INVISIBLE);
                            pb.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    },1500);
                    break;

                case R.id.atm:
                    mMap.clear();
                    pb.setBackgroundColor(getResources().getColor(R.color.black));
                    pb.setVisibility(View.VISIBLE);
                    url = getURL(lat, lng, "atm");
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list[0] =getNearbyPlaces.nearbyPlacesList;
                            System.out.println(list[0] +"=list");
                            BottomSheetNearby bottomSheetNearby=new BottomSheetNearby(list[0],"atm");
                            bottomSheetNearby.show(getSupportFragmentManager(), "bottomNearby");
                            pb.setVisibility(View.INVISIBLE);
                            pb.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    },1500);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
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
        pb.setVisibility(View.INVISIBLE);
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

    int d,mon,y,h,min,df,monf,yf,hf,minf;
    String address;
    DatePickerDialog dpd;
    Calendar now;

    @Override
    public void onBtnClick(String str) {
        address=str;
        now=Calendar.getInstance();


        y=now.get(Calendar.YEAR);
        mon=now.get(Calendar.MONTH);
        d=now.get(Calendar.DAY_OF_MONTH);
        h=now.get(Calendar.HOUR_OF_DAY);
        min=now.get(Calendar.MINUTE);


        DatePickerDialog dpd=new DatePickerDialog(this,this,y,mon,d);
        dpd.show();


    }

    @Override
    public void onCardClick() {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        now.set(Calendar.YEAR,i);
        now.set(Calendar.MONTH,i1);
        now.set(Calendar.DAY_OF_MONTH,i2);
        TimePickerDialog tpd=new TimePickerDialog(this, this,h,min,true);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        now.set(Calendar.HOUR_OF_DAY,i);
        now.set(Calendar.MINUTE,i1);

        System.out.println(now.get(Calendar.YEAR)+" "+
                now.get(Calendar.MONTH)+" "+
                now.get(Calendar.DAY_OF_MONTH)+" "+
                now.get(Calendar.HOUR_OF_DAY)+" "+
                now.get(Calendar.MINUTE));

        Calendar time=now;

//        NotifyMe.Builder notifyMe=new NotifyMe.Builder(getApplicationContext());
//        notifyMe.title(address).content("Notice").color(255,0,0,255)
//                .led_color(255,255,255,255).time(time).addAction(new Intent(),"Snooze",false)
//                .key("test").addAction(new Intent(),"Dismiss",true,false)
//                .addAction(new Intent(),"Done").large_icon(R.mipmap.ic_launcher_round);
//        notifyMe.build();

//        Intent alarmIntent = new Intent(this, MyBroadcastReceiver.class);
//        alarmIntent.putExtra("data", "Alarm manager example");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Calendar temp=Calendar.getInstance();
//
//        long mills=time.getTimeInMillis();
//        mills-=temp.getTimeInMillis();
//        long millsdup=SystemClock.elapsedRealtime();
//        mills+=millsdup;
//        System.out.println(mills+"  mills  " + millsdup);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            alarmManager.setExactAndAllowWhileIdle
//                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                            mills, pendingIntent);
//        else
//            alarmManager.setExact
//                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                            mills, pendingIntent);


        Intent alarmIntent = new Intent(this, MyBroadcastReceiver.class);

        alarmIntent.putExtra("address",address);

//        if (ReminderNotificationType.CHANGE_LENS.equals(notificationType)) {
//            alarmIntent.putExtra("NOTIFICATION_TYPE", "REMINDER");
//        } else {
//            alarmIntent.putExtra("NOTIFICATION_TYPE", "ORDER");
//        }

        Calendar temp=Calendar.getInstance();
//
        long mills=time.getTimeInMillis();
        mills-=temp.getTimeInMillis();
        long millsdup=SystemClock.elapsedRealtime();
        mills+=millsdup;
        System.out.println(mills+"  mills  " + millsdup);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, mills, pendingIntent);
    }
}

//    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification=new Notification.Builder(getApplicationContext())
//                .setContentTitle(address).setContentText("TESTING").setSmallIcon(R.mipmap.ic_launcher_round).build();
//        notification.flags|=Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0,notification);
//        Intent intent=new Intent(this,MyBroadcastReceiver.class);
//        intent.putExtra("cal",now);
//        intent.putExtra("address",address);
//        PendingIntent pendingIntent=PendingIntent.getBroadcast(this.getApplicationContext(),234324243,intent,0);
//        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
//        long mill=now.getTimeInMillis();
//        alarmManager.set(AlarmManager.RTC_WAKEUP,mill,pendingIntent);
//        Toast.makeText(this,"Event scheduled at "+h+":"+min+" "+d+"/"+(mon+1)+"/"+y,Toast.LENGTH_LONG).show();