package com.mertdogan.silentmodemanager;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;

import java.util.Calendar;
import java.util.List;


public class setButtonActivity extends AppCompatActivity {

    EditText startTime;
    EditText endTime;
    EditText titleText;
    private String[] modes={"Silent","Do Not Disturb"};
    private Spinner modeSpinner;
    private ArrayAdapter<String> dataAdapterForModes;
    int hour,hour2;
    int minute,minute2;
    boolean firstClickStart=true;
    boolean firstClickEnd=true;
    List<Integer> days;
    SQLiteLayer ds;
    WeekdaysPicker dayPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_button);
        ((Button) findViewById(R.id.deleteButtonEdit)).setVisibility(View.GONE);
        ds=new SQLiteLayer(this);

        //  initiate the edit text
        startTime = (EditText) findViewById(R.id.startTimeText);
        final String initialText=startTime.getText().toString();
        // perform click event listener on edit text
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                if(firstClickStart) {
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(setButtonActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet( TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String curTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        startTime.setText(curTime);
                        hour=selectedHour;
                        minute=selectedMinute;
                        firstClickStart=false;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });

        //  initiate the edit text
        endTime = (EditText) findViewById(R.id.endTimeText);
        // perform click event listener on edit text
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime2 = Calendar.getInstance();
                if(firstClickEnd) {
                    hour2 = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                    minute2 = mcurrentTime2.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(setButtonActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker2, int selectedHour2, int selectedMinute2) {
                        String curTime = String.format("%02d:%02d", selectedHour2, selectedMinute2);
                        endTime.setText(curTime);
                        hour2=selectedHour2;
                        minute2=selectedMinute2;
                        firstClickEnd=false;
                    }
                }, hour2, minute2, true);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();

            }
        });

        modeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        dataAdapterForModes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modes);
        dataAdapterForModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(dataAdapterForModes);

        titleText= (EditText) findViewById(R.id.titleText);

        dayPicker = (WeekdaysPicker) findViewById(R.id.dayPicker);
        dayPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {

            }
        });

        ((Button) findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(setButtonActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.applyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = dayPicker.getSelectedDays();
                if(!startTime.getText().toString().matches("") && !endTime.getText().toString().matches("") && !titleText.getText().toString().matches("")){
                    if(ds.insertData(startTime.getText().toString(), endTime.getText().toString(), days.toString(), modeSpinner.getSelectedItem().toString(), titleText.getText().toString(),0)){
                        Toast.makeText(setButtonActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(setButtonActivity.this, "Not Added", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(setButtonActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else{
                    Toast.makeText(setButtonActivity.this, "You have to fill all values", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
