package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.task61d.Database.DatabaseHelper;

public class Orderdetail extends AppCompatActivity {
    TextView Weight,Length,Width,Height,goodstype,quantity;
    TextView Sender,Receiver,Pickuptime,Dropofftime;
    ImageView photo;
    Button estimate;
    String weight1,length1,width1,height1,goodstype1;
    String year,month,day,hour,minute,sender,receiver,hour2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oderdetail);
        findid();
        //Get the data from the database and use it
        // to set the value of each control of the activity
        setvalue();
        estimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Orderdetail.this,Googlemap.class);
                startActivity(intent);
            }
        });
        }
    public void findid(){
         Weight=findViewById(R.id.Weight);
         estimate=findViewById(R.id.estimate);
         Length=findViewById(R.id.Length);;
         Width=findViewById(R.id.Width);;
         Height=findViewById(R.id.Height);;
         goodstype=findViewById(R.id.Type);
         quantity=findViewById(R.id.Quantity);;
         photo=findViewById(R.id.imageView);;
         Sender=findViewById(R.id.sender);
         Receiver =findViewById(R.id.receiver);
         Pickuptime=findViewById(R.id.pickuptime);
         Dropofftime=findViewById(R.id.Dropofftime);
    }
    public void setvalue(){
        DatabaseHelper dbHelper = new DatabaseHelper(this, "database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("order2", null, "id=?", new String[]{String.valueOf(Global.orderid)}, null, null, null);
        cursor.moveToFirst();
        Cursor cursor2 = db.query("user", null, "id=?", new String[]{String.valueOf(Global.id)}, null, null, null);
        cursor2.moveToFirst();
        sender=cursor2.getString(cursor2.getColumnIndexOrThrow("fullname"));
        byte[] photo1 = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
        if(photo1!=null) {
            Bitmap photo2 = getBitmap(photo1);
            photo.setImageBitmap(photo2);
        }
        weight1= cursor.getString(cursor.getColumnIndexOrThrow("weight"));
        length1= cursor.getString(cursor.getColumnIndexOrThrow("length"));
        width1= cursor.getString(cursor.getColumnIndexOrThrow("weight"));
        height1= cursor.getString(cursor.getColumnIndexOrThrow("height"));
        goodstype1= cursor.getString(cursor.getColumnIndexOrThrow("itemtype"));
        year= cursor.getString(cursor.getColumnIndexOrThrow("year"));
        hour= cursor.getString(cursor.getColumnIndexOrThrow("hour"));
        day= cursor.getString(cursor.getColumnIndexOrThrow("day"));
        minute= cursor.getString(cursor.getColumnIndexOrThrow("minute"));
        month= cursor.getString(cursor.getColumnIndexOrThrow("month"));
        receiver= cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
        hour2= String.valueOf(Integer.valueOf(hour)+1);
        Weight.setText("Weight:                   "+weight1+"kg");
        Height.setText("Height:                   "+height1+"m");
        Width.setText("Width:                   "+width1+"m");
        goodstype.setText("goodstype:                   "+goodstype1);
        Length.setText("Length:                   "+length1+"m");
        quantity.setText("quantity:                    1");
        Sender.setText(sender);
        Receiver.setText(receiver);
        Pickuptime.setText(day+"/"+month+"/"+year);
        Dropofftime.setText(day+"/"+month+"/"+year);
    }
    // fuction to transfer bytes[] to Bitmap
    public static Bitmap getBitmap(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
}


