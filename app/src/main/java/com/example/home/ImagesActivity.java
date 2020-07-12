package com.example.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private DatabaseReference databaseReference;
    private List<Upload> uploadList;
    private String email,loc;
    private ProgressBar progressBar;
    private int count=0;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recycler_view_images);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar=findViewById(R.id.prgbar);
        tv=findViewById(R.id.tv);
        tv.setVisibility(View.GONE);

        loc=getIntent().getStringExtra("location");
        long val = 0;
        for(int i=0;i<loc.length();i++)
            val+=(long)loc.charAt(i);

        String st=Long.toString(val);
        uploadList=new ArrayList<>();
        email=MainActivity.email;
        System.out.println(email+" "+st);
        databaseReference= FirebaseDatabase.getInstance().getReference(email.substring(0,email.length()-4)+st);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                     Upload upload=postSnapshot.getValue(Upload.class);
                     uploadList.add(upload);
                     count++;
                 }
                 imageAdapter=new ImageAdapter(ImagesActivity.this,uploadList);
                 System.out.println(count+"=count");
                 recyclerView.setAdapter(imageAdapter);
                 progressBar.setVisibility(View.INVISIBLE);
                 if(count == 0)
                     tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
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
}