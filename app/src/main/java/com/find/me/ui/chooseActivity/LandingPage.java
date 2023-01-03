package com.find.me.ui.chooseActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.find.me.R;
import com.find.me.ui.signUpscreen.SignUpActivity;

import java.util.ArrayList;
import java.util.List;

import com.find.me.ShowUsersActivity;
import com.find.me.dbHelper.UsersDB;
import com.find.me.model.User;
import com.find.me.ui.dashboard.MainActivity;
import com.find.me.ui.loginScreen.LoginActivity;
import com.find.me.utils.Preferences;

public class LandingPage extends AppCompatActivity implements View.OnClickListener{

    private TextView signIn, signUp,privacy;
    Button showUsers;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        signIn = (TextView) findViewById(R.id.signInButton);
        signUp = (TextView) findViewById(R.id.signUpButton);
        showUsers = (Button) findViewById(R.id.showUsersButton);
        checkCredentialsPref(Preferences.readString(this,"username"),Preferences.readString(this,"password"));
        if (!hasPermissions(LandingPage.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(LandingPage.this, PERMISSIONS, 150);
        }
        else {
            checkInternet();
        }
        checkAndRequestPermissions();
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        showUsers.setOnClickListener(this);

        //to access dev tools in chrome and see the database contents
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                Intent intent0 = new Intent(LandingPage.this, LoginActivity.class);
                startActivity(intent0);
                break;
            case R.id.signUpButton:
                Intent intent1 = new Intent(LandingPage.this, SignUpActivity.class);
                startActivity(intent1);
                break;
            case R.id.showUsersButton:
                Intent intent2 = new Intent(LandingPage.this, ShowUsersActivity.class);
                startActivity(intent2);
                break;
        }
    }
    private void checkCredentialsPref(String username, String password){



            UsersDB db = new UsersDB(this);
            User user = new User(username, password);

            //checks that the user credentials are correct
            if(db.checkCredentials(user)){
                Preferences.readString(this,"username");
                Preferences.readString(this,"password");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();


        }
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
    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int coarse_location = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int phone_call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int notification = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(LandingPage.this);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LandingPage.this.startActivityForResult(myIntent, 100);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //goButton.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);

                    SharedPreferences sharedPref = LandingPage.this.getSharedPreferences("location", 0);
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
    void checkInternet() {
        if (isNetworkAvailable(LandingPage.this)) {
            checkGPSStatus();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LandingPage.this);
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
