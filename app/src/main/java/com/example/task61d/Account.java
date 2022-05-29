package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task61d.Database.DatabaseHelper;

import java.io.ByteArrayOutputStream;

public class Account extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    TextView username;
    TextView phonenumber;
    TextView fullname;
    ImageView iv_photo;
    Bitmap photo1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        username = findViewById(R.id.Username);
        phonenumber = findViewById(R.id.Phonenumber);
        fullname = findViewById(R.id.Fullname);
        iv_photo = findViewById(R.id.iv_photo);
        dbHelper = new DatabaseHelper(this, "database", null, 1);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, "id=?", new String[]{String.valueOf(Global.id)}, null, null, null);
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(Account.this, "please try again", Toast.LENGTH_SHORT).show();
        } else {
            byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
            String username1 = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String fullname1 = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            int phonenumber1 = cursor.getInt(cursor.getColumnIndexOrThrow("phonenumber"));
            photo1 = getBitmap(photo);
            iv_photo.setImageBitmap(photo1);
            username.setText("username:"+username1);
            fullname.setText("full name:"+fullname1);
            phonenumber.setText("phone number:"+(phonenumber1)+"");
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intent=new Intent(Account.this,Home.class);
                startActivity(intent);
                break;
            case R.id.account:
                break;
            case R.id.order:
                Intent intent2=new Intent(Account.this,Myorder.class);
                startActivity(intent2);
                break;
            case R.id.help:
                Intent intent3=new Intent(Account.this,Responsebot.class);
                startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //Converting bytes[] to Bitmap
    public static Bitmap getBitmap(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

}