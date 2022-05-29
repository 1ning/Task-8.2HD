package com.example.task61d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Createorder extends AppCompatActivity {
    DatePicker datePicker;
    TimePicker timePicker;
    EditText name;
    Button next;
    int year, month, day;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorder);
        long now = System.currentTimeMillis() - 1000;
        Calendar calendar = Calendar.getInstance();
        findid(calendar);
        //Date selector
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePicker.setMinDate(now);
                Calendar maxCalendar = Calendar.getInstance();
                maxCalendar.add(Calendar.YEAR, 0);
                datePicker.setMaxDate(maxCalendar.getTimeInMillis());
                Createorder.this.year = year;
                Createorder.this.month = (monthOfYear + 1);
                Createorder.this.day = dayOfMonth;
            }
        });
        //Time selector
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view
                    , int hourOfDay, int minute) {
                Createorder.this.hour = hourOfDay;
                Createorder.this.minute = minute;
            }
        });
        //Skip to the next activity for filling order information
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name1 = name.getText().toString().trim();
                if (name1 != null) {
                    if (day>calendar.get(Calendar.DATE)||month>calendar.get(Calendar.MONTH)||hour > calendar.get(Calendar.HOUR)) {
                        Intent intent = new Intent(Createorder.this, Createorder2.class);
                        intent.putExtra("name", name1);
                        intent.putExtra("year", year);
                        intent.putExtra("month", month );
                        intent.putExtra("day", day);
                        intent.putExtra("hour", hour );
                        intent.putExtra("minute", minute);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Createorder.this, "There is a problem with your choice of time, we need at least an hour to prepare", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void findid(Calendar calendar){
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        name = findViewById(R.id.name);
        next = findViewById(R.id.next);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }
}