package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.task61d.Database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class Myorder extends AppCompatActivity {
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
        //Get the data we need for this activity from the database
        List<Truck> list = queryAllTrucksData();
        if (this != null) {
            MyorderAdapter myorderAdapter = new MyorderAdapter(this, list);
            mRecyclerView.setAdapter(myorderAdapter);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Myorder.this,Createorder.class);
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
                Intent intent2=new Intent(Myorder.this,Home.class);
                startActivity(intent2);
                break;
            case R.id.account:
                Intent intent=new Intent(Myorder.this,Account.class);
                startActivity(intent);
                break;
            case R.id.order:
                break;
            case R.id.help:
                Intent intent3=new Intent(Myorder.this,Responsebot.class);
                startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    //Get the data we need for this activity from the database
    public List<Truck> queryAllTrucksData(){
        Cursor cursor = db.query("order2",null,"userid=?",new String[]{Global.id+""},null,null,null);
        List<Truck> list = new ArrayList<>();
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("cartype"));
                Cursor cursor2 = db.query("truck",null,"type=?",new String[]{type},null,null,null,"0,1");
                cursor2.moveToFirst();
                Truck model = new Truck();
                model.id= cursor.getString(cursor.getColumnIndexOrThrow("id"));
                model.type=type;
                model.context=cursor2.getString(cursor2.getColumnIndexOrThrow("context"));
                list.add(model);
                cursor.moveToNext();
                cursor2.close();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

}
