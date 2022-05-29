package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.task61d.Database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    FloatingActionButton create;
    private SQLiteDatabase db;
    private RecyclerView mRecyclerView;
    public List<Truck>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        dbHelper = new DatabaseHelper(this, "database", null, 1);
        db = dbHelper.getWritableDatabase();
        mRecyclerView = findViewById(R.id.recycler_view);
        create=findViewById(R.id.Create);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Get the data you need from the database
        List<Truck> list = queryAllTrucksData();
        //Initialise the recycleview after getting the data
        if (this != null) {
            TruckAdapter truckAdapter = new TruckAdapter(this, list);
            mRecyclerView.setAdapter(truckAdapter);
        }
        //Skip to order creation activity
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this,Createorder.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.home:
            break;
           case R.id.account:
           Intent intent=new Intent(Home.this,Account.class);
           startActivity(intent);
           break;
           case R.id.order:
           Intent intent4=new Intent(Home.this,Myorder.class);
           startActivity(intent4);
           break;
           case R.id.help:
           Intent intent3=new Intent(Home.this,Responsebot.class);
           startActivity(intent3);
           break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public List<Truck> queryAllTrucksData(){
        Cursor cursor = db.query("truck",null,null,null,null,null,null,null);
        List<Truck> list = new ArrayList<>();
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String context = cursor.getString(cursor.getColumnIndexOrThrow("context"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                Truck model = new Truck();
                model.context=context;
                model.type=type;
                list.add(model);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

}
