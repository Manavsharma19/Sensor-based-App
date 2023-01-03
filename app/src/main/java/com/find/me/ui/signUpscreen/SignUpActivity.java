package com.find.me.ui.signUpscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.find.me.R;
import com.find.me.fragments.DatePickerFragment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

import com.find.me.dbHelper.UsersDB;
import com.find.me.model.User;
import com.find.me.utils.CalendarUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    final String TAG = "SignUpActivity";
    private EditText ETUsername, ETPassword, ETphone, ETiqama;
    private TextView signUpButton, cancelButton, date_display;
    private int day, month, year;
    private Calendar date;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ETUsername = (EditText) findViewById(R.id.ETUsername);
        ETPassword = (EditText) findViewById(R.id.ETPassword);
        ETphone = (EditText) findViewById(R.id.pnumber);
        ETiqama = (EditText) findViewById(R.id.iqama_no);
        //display date
        Calendar c = Calendar.getInstance();
        date = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        signUpButton = (TextView) findViewById(R.id.signUpButton);
        cancelButton = (TextView) findViewById(R.id.cancelButton);
        date_display = (TextView) findViewById(R.id.date_view);
        date_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        signUpButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        ETUsername.requestFocus();

        //to access dev tools in chrome and see the database contents
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpButton:
                checkCredentials();
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    private void checkCredentials(){
        // get db and StringBuilder objects
        UsersDB db;
        User user;

        //checks that username and password are not empty
            if(!ETUsername.getText().toString().equals("") && !ETPassword.getText().toString().equals("") && !ETphone.getText().toString().equals("") && !ETiqama.getText().toString().equals("") && !date_display.getText().toString().equals("")){
            //proceed

            db = new UsersDB(this);

            String username = ETUsername.getText().toString();
            String password = ETPassword.getText().toString();
            String phone = ETphone.getText().toString();
            String iqama = ETiqama.getText().toString();
            String dob = date_display.getText().toString();

            //need to check the database to see if the value already exists
            Log.d(TAG, "Did the user already exist?"+db.userExists(username));

            //if the user exists
            if(db.userExists(username)){
                toastIt("username "+username+" already exists");
            }
            else{//if the user does not exist, then it inserts it
                user = new User(username, password,iqama,dob,phone);
                long insertId = db.insertUser(user);

                toastIt("Successfully added "+username);
                finish();
            }
        }
        else{//don't proceed
            toastIt("cannot have empty fields");
        }
    }
    @Override
    public void onDateSet(DatePicker view, int _year, int _month, int _day) {

        year = _year;
        month = _month;
        day = _day;
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);

        LocalDate ldate = date.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        date_display.setText( CalendarUtils.dateToString(ldate));
    }

    private void toastIt(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
    }
}
