package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.task61d.Database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    EditText Username;
    EditText Password;
    Button Login;
    Button Signup;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Username=(EditText) findViewById(R.id.Username);
        Password=(EditText) findViewById(R.id.Password);
        Login=(Button)findViewById(R.id.Create);
        Signup=(Button)findViewById(R.id.Signup);
        dbHelper= new DatabaseHelper(this, "database", null, 1);
        //After clicking the login button, the information entered is matched
        // and if successful, you will be redirected to the home page
        Login.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            String user = Username.getText().toString().trim();
            String pwd = Password.getText().toString().trim();
            if (user == null ) {
                Toast.makeText(MainActivity.this,"Username cannot be empty, please re-enter", Toast.LENGTH_SHORT).show();}
            else if (pwd == null) {
                Toast.makeText(MainActivity.this,"Password cannot be empty, please re-enter", Toast.LENGTH_SHORT).show();}
            else {
                db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("user", null, "username=? and password=?", new String[]{user, pwd}, null, null, null);
                cursor.moveToFirst();
                if (cursor == null||cursor.getCount()== 0) {
                    Toast.makeText(MainActivity.this, "User name and password entered incorrectly, please try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    Global.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    Global.sender = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                    Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,Home.class);
                    startActivity(intent);
                    finish();
                     }
                cursor.close();
                db.close();
                 } }
                   });
        //Jump to the registration screen
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(MainActivity.this,Signupactivity.class);
                startActivity(intent2);
            }
        });
    }
}
