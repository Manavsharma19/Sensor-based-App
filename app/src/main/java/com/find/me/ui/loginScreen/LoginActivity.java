package com.find.me.ui.loginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.find.me.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.List;

import com.find.me.dbHelper.UsersDB;
import com.find.me.mapData.Constants;
import com.find.me.mapData.MYLocation1;
import com.find.me.model.User;
import com.find.me.ui.dashboard.MainActivity;
import com.find.me.utils.Preferences;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ETUsername, ETPassword;
    private TextView signInButton;
    ImageView cancelButton;
    public static String token;

    private LocationRequest locationRequest;


    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        ETUsername = (EditText) findViewById(R.id.ETUsername);
        ETPassword = (EditText) findViewById(R.id.ETPassword);


        signInButton = (TextView) findViewById(R.id.signInButton);
        cancelButton = (ImageView) findViewById(R.id.cancelButton);

        signInButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        ETUsername.requestFocus();
        checkAndRequestPermissions();
        //to access dev tools in chrome and see the database contents
        Stetho.initializeWithDefaults(this);
        if (!hasPermissions(LoginActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, 150);
        }
        else {
            checkInternet();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signInButton:
                if (isGPSEnabled()){}
                else{
                    turnOnGPS();
                    return;
                }
                checkCredentials();
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    private void checkCredentials(){

        if(!ETUsername.getText().toString().equals("") && !ETPassword.getText().toString().equals("")){
            //proceed

            String username = ETUsername.getText().toString();
            String password = ETPassword.getText().toString();

            UsersDB db = new UsersDB(this);
            User user = new User(username, password);

            //checks that the user credentials are correct
            if(db.checkCredentials(user)){
                toastIt("Successfully logged in");
                Preferences.writeString(this,"username",ETUsername.getText().toString());
                Preferences.writeString(this,"password",ETPassword.getText().toString());
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                toastIt("invalid credentials");
            }
        }
        else{//don't proceed
            toastIt("cannot have empty fields");
        }
    }
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(LoginActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LoginActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private void toastIt(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }
    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int coarse_location = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int phone_call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int notification = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }    if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
/*        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }

        if (notification != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }

        if (phone_call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarse_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            int REQUEST_ID_MULTIPLE_PERMISSIONS = 10001;
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private void getLocation1()
    {
        MYLocation1.LocationResult locationResult = new MYLocation1.LocationResult() {
            @Override
            public void gotLocation(final Location location) {
                if (location != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // change UI elements here
                            Constants.location = location;
                            //progressBar.setVisibility(View.GONE);

                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("location", 0);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("lat", Double.toString(location.getLatitude()));
                            editor.putString("lng", Double.toString(location.getLongitude()));
                            editor.apply();

                        }
                    });
                    //Got the location!


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // change UI elements here
                            Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                            //     ShowDialogForDialog("start");
                        }
                    });


                }
            }
        };
        MYLocation1 myLocation = new MYLocation1();
        myLocation.getLocation(this, locationResult);
    }
    private void checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LoginActivity.this.startActivityForResult(myIntent, 100);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        } else {
            getLocation1();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //goButton.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);

                    SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("location", 0);
                    String lat = sharedPref.getString("lat", ""); //0 is the default value
                    String lng = sharedPref.getString("lng", ""); //0 is the default value



                    if (!lat.equals("") && !lng.equals("")){
                        //   Double lati =Double.valueOf(lat);
                        //   Double longi =Double.valueOf(lng);
                    }

                }
            }, 2500);


        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 150:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission with request code 1 granted
                    //   Toast.makeText(this, "Permission Granted" , Toast.LENGTH_LONG).show();
                    checkInternet();
                } else {
                    //permission with request code 1 was not granted

                    Toast.makeText(this, "Permission was not Granted" , Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkGPSStatus();
        // checkGPSStatus();
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    void checkInternet() {
        if (isNetworkAvailable(LoginActivity.this)) {
            checkGPSStatus();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setTitle("Internet Error");
            alertDialog.setMessage("Internet is not enabled! ");
            alertDialog.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            checkInternet();
                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            System.exit(0);

                        }
                    });
            alertDialog.show();

        }
    }
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // if no network is availablgoButton_ide networkInfo will be null, otherwise check if we are connected
        try {
            @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }

        } catch (Exception e) {
            Log.e("UtilsClass", "isNetworkAvailable()::::" + e.getMessage());
        }
        return false;
    }



}
