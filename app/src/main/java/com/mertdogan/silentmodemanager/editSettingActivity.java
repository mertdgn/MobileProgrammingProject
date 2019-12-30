package com.mertdogan.silentmodemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
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


public class editSettingActivity extends AppCompatActivity{
    EditText startTime;
    EditText endTime;
    EditText titleText;
    private Spinner modeSpinner;
    WeekdaysPicker dayPicker;
    int hour,hour2;
    int minute,minute2;
    boolean firstClickStart=true;
    boolean firstClickEnd=true;
    private String[] modes={"Silent","Do Not Disturb"};
    private ArrayAdapter<String> dataAdapterForModes;
    List<Integer> days;
    SQLiteLayer ds;
    SilentModeSetting sms;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_button);
        final Intent intent=this.getIntent();
        Bundle bundle = intent.getExtras();
        ds=new SQLiteLayer(this);
       // SilentModeSetting sms= new SilentModeSetting(bundle.getString("startTime"),bundle.getString("endTime"), bundle.getStringArrayList("days"), bundle.getString("mode"), bundle.getString("title"));
        sms= (SilentModeSetting) bundle.getSerializable("data");

        startTime = (EditText) findViewById(R.id.startTimeText);
        endTime = (EditText) findViewById(R.id.endTimeText);
        titleText = (EditText)findViewById(R.id.titleText);
        modeSpinner = (Spinner) findViewById(R.id.modeSpinner);

        startTime.setText(sms.getStartTime());
        endTime.setText(sms.getEndTime());
        titleText.setText(sms.getTitle());
        dataAdapterForModes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modes);
        dataAdapterForModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(dataAdapterForModes);

        if(sms.getMode().equals("Silent")){
            modeSpinner.setSelection(0);
        }
        else{
            modeSpinner.setSelection(1);
        }
        //  initiate the edit text
        startTime = (EditText) findViewById(R.id.startTimeText);
        final String initialText=startTime.getText().toString();
        // perform click event listener on edit text
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                if(firstClickStart) {
                    hour = Integer.parseInt(startTime.getText().toString().substring(0,2));
                    minute = Integer.parseInt(startTime.getText().toString().substring(startTime.getText().toString().length() - 2));
                }

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(editSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
                    hour2 = Integer.parseInt(sms.getEndTime().substring(0,2));
                    minute2 = Integer.parseInt(endTime.getText().toString().substring(endTime.getText().toString().length() - 2));
                }
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(editSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        dayPicker = (WeekdaysPicker) findViewById(R.id.dayPicker);
        dayPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {

            }
        });
        dayPicker.setSelectedDays(sms.getDays());

       ((Button) findViewById(R.id.deleteButtonEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ds.deleteData(sms);
                Toast.makeText(editSettingActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(editSettingActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editSettingActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        ((Button) findViewById(R.id.applyButton)).setText("Save");
        ((Button) findViewById(R.id.applyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = dayPicker.getSelectedDays();
                if(!startTime.getText().toString().matches("") && !endTime.getText().toString().matches("") && !titleText.getText().toString().matches("")) {
                    if (ds.updateData(sms, startTime.getText().toString(), endTime.getText().toString(), days.toString(), modeSpinner.getSelectedItem().toString(), titleText.getText().toString(), 0)) {
                        Toast.makeText(editSettingActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(editSettingActivity.this, "Not Updated", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(editSettingActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else{
                    Toast.makeText(editSettingActivity.this, "You have to fill all values", Toast.LENGTH_SHORT).show();
                }
            }


        });



    }


}
