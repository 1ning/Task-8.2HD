package com.example.task61d.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String sqlStatements = "create table user ("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "password text, "
            + "phonenumber integer, "
            + "photo blob, "
            + "fullname text)";
    private static final String CREATE_trucks = "create table truck ("
            + "id integer primary key autoincrement,"
            + "type text,"
            + "context text)";

    private static final String CREATE_orders = "create table order2 ("
            + "id integer primary key autoincrement,"
            + "year integer,"
            + "month integer,"
            + "day integer,"
            + "hour integer,"
            + "minute integer,"
            + "itemtype text,"
            + "cartype text,"
            + "weight double,"
            + "width double,"
            + "receiver text,"
            + "photo blob, "
            + "height double,"
            + "length double,"
            + "location text,"
            + "evaluation text,"
            + "rating text,"
            + "quantity integer,"
            + "userid integer)";

    private Context context;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlStatements);
        db.execSQL(CREATE_trucks);
        db.execSQL(CREATE_orders);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
