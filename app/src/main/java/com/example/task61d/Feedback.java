package com.example.task61d;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.task61d.Database.DatabaseHelper;

import java.util.ArrayList;

public class Feedback extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private EditText tvText;
    private Button  Complete;
    String feedback;
    String rating1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        RatingBar rb_normal = (RatingBar) findViewById(R.id.rb_normal);
        tvText = findViewById(R.id.feedback);
        btnSpeak = findViewById(R.id.Transform);
        Complete=findViewById(R.id.Complete);
        dbHelper = new DatabaseHelper(this, "database", null, 1);
        //Users can rate via the rating bar
        rb_normal.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(Feedback.this, "rating:" + String.valueOf(rating),
                        Toast.LENGTH_LONG).show();
                rating1=String.valueOf(rating);
            }
        });
        //When the user clicks on finish, the content is checked
        // to see if it is empty and then uploaded to the database
        Complete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(rating1!=null&&feedback!=null)
                                            {
                                                db = dbHelper.getWritableDatabase();
                                                ContentValues values = new ContentValues();
                                                values.put("rating", rating1);
                                                values.put("evaluation",feedback);
                                                long result = db.insert("order2", null, values);
                                                db.close();
                                                dbHelper.close();
                                                Toast.makeText(getApplicationContext(), "Your order is complete ", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Feedback.this, Home.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
        //When the user clicks on the button with the microphone icon,
        // they can enter their voice and it will be converted into text
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvText.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    //When the voice conversion request is completed, the input voice
    //is converted to text and displayed in the content box
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(text.get(0));
                    feedback=text.get(0);
                }
                break;
        }
    }
}