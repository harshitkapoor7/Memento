package com.example.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class MyReminders extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        ReminderItemsAdapter.OnEditClickListener {

    ArrayList<String> dt, db, dTime, dDate;
    RecyclerView recyclerView;
    ReminderItemsAdapter reminderItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reminders);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent intent = getIntent();

        dt = (ArrayList<String>) intent.getSerializableExtra("dataTitle");
        db = (ArrayList<String>) intent.getSerializableExtra("dataBody");
        dTime = (ArrayList<String>) intent.getSerializableExtra("dataTime");
        dDate = (ArrayList<String>) intent.getSerializableExtra("dataDate");


        if (dt != null) {
            recyclerView = findViewById(R.id.recycler_view_reminder);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            updateList(db,dt,dTime,dDate);
            sortList(db,dt,dTime,dDate);
            SharedPreferences sharedPreferences=getSharedPreferences(MainActivity.email,MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            Gson gson1=new Gson();
            String json=gson1.toJson(dt);
            String json1=gson1.toJson(db);
            String json6=gson1.toJson(dTime);
            String json7=gson1.toJson(dDate);
            editor.putString("dataTitle",json);
            editor.putString("dataBody",json1);
            editor.putString("dataTime",json6);
            editor.putString("dataDate",json7);
            editor.apply();
            reminderItemsAdapter = new ReminderItemsAdapter(dt, db, dTime, dDate, this);
            recyclerView.setAdapter(reminderItemsAdapter);

        }
    }

    public void updateList(ArrayList<String> db, ArrayList<String> dt, ArrayList<String> dTime, ArrayList<String> dDate){
        for(int i=0;i<dDate.size();i++){
            String first[]=dDate.get(i).split("/");

            int d1=Integer.parseInt(first[0]);
            int m1=Integer.parseInt(first[1]);
            int y1=Integer.parseInt(first[2]);

            String[] tfirst=dTime.get(i).split(":");

            int h1=Integer.parseInt(tfirst[0]);
            int s1=Integer.parseInt(tfirst[1]);
            Calendar now=Calendar.getInstance();
            Calendar rem=Calendar.getInstance();
            rem.set(y1,m1,d1,h1,s1);
            if(now.getTimeInMillis()>rem.getTimeInMillis()){
                db.remove(i);
                dt.remove(i);
                dDate.remove(i);
                dTime.remove(i);
                i--;
            }
        }
    }

    public void sortList(ArrayList<String> db,ArrayList<String> dt,ArrayList<String> dTime,ArrayList<String> dDate){
        for(int i=0;i<dDate.size();i++){
            for(int j=i+1;j<dDate.size();j++){
                String[] first=dDate.get(i).split("/");
                String[] second=dDate.get(j).split("/");
                int d1=Integer.parseInt(first[0]);
                int d2=Integer.parseInt(second[0]);

                int m1=Integer.parseInt(first[1]);
                int m2=Integer.parseInt(second[1]);
                int y1=Integer.parseInt(first[2]);
                int y2=Integer.parseInt(second[2]);

                if(y1>y2 || (y1 == y2 && m1>m2) || (y1 == y2 && m1 == m2 && d1>d2)){
                    Collections.swap(db,i,j);
                    Collections.swap(dt,i,j);
                    Collections.swap(dTime,i,j);
                    Collections.swap(dDate,i,j);
                }
                else if(y1 == y2 && m1 == m2 && d1 == d2){
                    String[] tfirst=dTime.get(i).split(":");
                    String[] tsecond=dTime.get(j).split(":");

                    int h1=Integer.parseInt(tfirst[0]);
                    int h2=Integer.parseInt(tsecond[0]);
                    int s1=Integer.parseInt(tfirst[1]);
                    int s2=Integer.parseInt(tsecond[1]);

                    if(h1>h2 || (h1 == h2 && s1>s2)){
                        Collections.swap(db,i,j);
                        Collections.swap(dt,i,j);
                        Collections.swap(dTime,i,j);
                        Collections.swap(dDate,i,j);
                    }
                }

            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onEditClick(String str) {
        callDpd(str);
    }
    String address,body;
    int y,mon,d,h,min;
    Calendar now;

    public void callDpd(String str) {
        address = str;
        now = Calendar.getInstance();

        y = now.get(Calendar.YEAR);
        mon = now.get(Calendar.MONTH);
        d = now.get(Calendar.DAY_OF_MONTH);
        h = now.get(Calendar.HOUR_OF_DAY);
        min = now.get(Calendar.MINUTE);

        DatePickerDialog dpd = new DatePickerDialog(this, this, y, mon, d);
        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        now.set(Calendar.YEAR, i);
        now.set(Calendar.MONTH, i1);
        now.set(Calendar.DAY_OF_MONTH, i2);
        TimePickerDialog tpd = new TimePickerDialog(this, this, h, min, true);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        now.set(Calendar.HOUR_OF_DAY, i);
        now.set(Calendar.MINUTE, i1);


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.dialog_box, null);
        final EditText editText = view.findViewById(R.id.note);
        Button cancelIt = view.findViewById(R.id.cancel_it);
        Button save = view.findViewById(R.id.save);


        cancelIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = editText.getText().toString();
                editText.setText("");
                y = now.get(Calendar.YEAR);
                mon = now.get(Calendar.MONTH);
                d = now.get(Calendar.DAY_OF_MONTH);
                h = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);

                System.out.println(now.get(Calendar.YEAR) + " " +
                        now.get(Calendar.MONTH) + " " +
                        now.get(Calendar.DAY_OF_MONTH) + " " +
                        now.get(Calendar.HOUR_OF_DAY) + " " +
                        now.get(Calendar.MINUTE));

                SharedPreferences sp=getSharedPreferences(MainActivity.email,MODE_PRIVATE);



                ArrayList<String> dataTitle,dataBody,dataTime,dataDate;

                Gson gson = new Gson();
                String json4 = sp.getString("dataTime", null);
                String json5 = sp.getString("dataDate", null);
                String json3 = sp.getString("dataTitle", null);
                String json2 = sp.getString("dataBody", null);
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                dataTitle = gson.fromJson(json3, type);
                dataBody = gson.fromJson(json2, type);
                dataTime = gson.fromJson(json4, type);
                dataDate = gson.fromJson(json5, type);


                if (dataTitle == null) {
                    dataTitle = new ArrayList<String>();
                    dataBody = new ArrayList<String>();
                    dataTime = new ArrayList<String>();
                    dataDate = new ArrayList<String>();
                }

                dataTitle.add(address);
                dataBody.add(body);
                if (min < 10)
                    dataTime.add(h + ":0" + min);
                else
                    dataTime.add(h + ":" + min);
                dataDate.add(d + "/" + (mon + 1) + "/" + y);

                int request_Code = 0;
                for (int i = 0; i < address.length(); i++)
                    request_Code += ((int) address.charAt(i) * (int) address.charAt(i));
                if (body != null) {
                    for (int i = 0; i < body.length(); i++) {
                        request_Code += ((int) body.charAt(i) * (int) body.charAt(i));
                    }
                }
                request_Code %= Integer.MAX_VALUE;

                SharedPreferences.Editor editor = sp.edit();
                Gson gson1 = new Gson();
                String json = gson1.toJson(dataTitle);
                String json1 = gson1.toJson(dataBody);
                String json6 = gson1.toJson(dataTime);
                String json7 = gson1.toJson(dataDate);
                editor.putString("dataTitle", json);
                editor.putString("dataBody", json1);
                editor.putString("dataTime", json6);
                editor.putString("dataDate", json7);
                editor.apply();

                Calendar time = now;

                Intent alarmIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);

                alarmIntent.putExtra("address", address);
                alarmIntent.putExtra("body", body);
                alarmIntent.putExtra("request_code", request_Code);

                Calendar temp = Calendar.getInstance();


                long mills = time.getTimeInMillis();
                mills -= temp.getTimeInMillis();
                long millsdup = SystemClock.elapsedRealtime();
                mills += millsdup;
                System.out.println(mills + "  mills  " + millsdup);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), request_Code, alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, mills, pendingIntent);

                Toast.makeText(getApplicationContext(), "Alarm set at " + h + ":" + min + " on " + d + "/" + (mon + 1) + "/" + y, Toast.LENGTH_LONG).show();
                alertDialog.dismiss();

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();

    }
}