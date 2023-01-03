package com.find.me.fragments;

import static android.content.Context.SENSOR_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.find.me.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;


import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.find.me.appCheck.MyApplication;
import com.find.me.dbHelper.UsersDB;
import com.find.me.mapData.Constants;
import com.find.me.model.User;
import com.find.me.ui.emergencyContacts.EmergencyList;
import com.find.me.ui.relativeList.RelativeContactList;
import com.find.me.utils.Preferences;


public class HomeFragment extends Fragment implements SensorEventListener2 {
    MapView mMapView;
    Context context;
    TextView relative, emergency;
    int check = 0;
    String laturl;
    String longurl;
    TextView smsbtn;
    SweetAlertDialog sweetAlertDialogLoading;
    private GoogleMap googleMap;
    SensorManager sensor;

    ArrayList<String> mobileArrays = new ArrayList<String>();
    ArrayList<String> nameArrays = new ArrayList<String>();
    public static ArrayList<User> users;
    public static ArrayList<User> user;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        context = getContext();
        sensor = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sweetAlertDialogLoading = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialogLoading.setTitleText("Fetching Latest Feed..");
        sweetAlertDialogLoading.setContentText("Please Wait...");
        sweetAlertDialogLoading.setCancelable(false);
        sweetAlertDialogLoading.show();
        UsersDB dbb = new UsersDB(getActivity());

        //START LISTVIEW CODE
        ///////////////////////////////////////////

        //list of data to display
        users = dbb.getRelativeList();
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        relative = view.findViewById(R.id.relContact);
        emergency = view.findViewById(R.id.emContact);
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RelativeContactList.class);
                startActivity(intent);
            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EmergencyList.class);
                startActivity(intent);

            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                com.google.android.gms.maps.model.LatLng sydney;
                Log.e("-ppp", "loc: " + Constants.location);
                if (Constants.location != null) {
                    sydney = new com.google.android.gms.maps.model.LatLng(Constants.location.getLatitude(), Constants.location.getLongitude());
                } else {
                    //To retrieve
                    SharedPreferences sharedPref = context.getSharedPreferences("location", 0);
                    String lat = sharedPref.getString("lat", "12"); //0 is the default value
                    String lng = sharedPref.getString("lng", "21"); //0 is the default value
                    Double lati = Double.valueOf(lat);
                    Double longi = Double.valueOf(lng);
                    sydney = new com.google.android.gms.maps.model.LatLng(lati, longi);
                }

                // For zooming automatically to the location of the marker
                com.google.android.gms.maps.model.CameraPosition cameraPosition = new com.google.android.gms.maps.model.CameraPosition.Builder().target(sydney).zoom(15).build();
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (sweetAlertDialogLoading.isShowing()) {
                    sweetAlertDialogLoading.cancel();
                }
            }
        });
        smsbtn = view.findViewById(R.id.sendSms);
        smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String url = "";
                UsersDB dbb = new UsersDB(getActivity());

                //START LISTVIEW CODE
                ///////////////////////////////////////////

                //list of data to display
                users = dbb.getRelativeList();
                user = dbb.getUsers();
                if (users.isEmpty()) {
                    Toast.makeText(getActivity(), "Add emergency contacts", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (Constants.location != null) {
//                     url =( "http://maps.google.com/maps?q=loc:" +"<"+Constants.location.getLatitude()+","+ Constants.location.getLongitude()+">");
                    url = ("https://www.google.com/maps/search/?api=1&query=" + Constants.location.getLatitude() + "," + Constants.location.getLongitude());
                    laturl = String.valueOf(Constants.location.getLatitude());
                    longurl = String.valueOf(Constants.location.getLongitude());

                } else {
                    //To retrieve
                    SharedPreferences sharedPref = context.getSharedPreferences("location", 0);
                    String lat = sharedPref.getString("lat", "21"); //0 is the default value
                    String lng = sharedPref.getString("lng", "42"); //0 is the default value
                    Double lati = Double.valueOf(lat);
                    Double longi = Double.valueOf(lng);
                    laturl = lat;
                    longurl = lng;
//                    url =( "http://maps.google.com/maps?q=loc:" +"<"+lati+","+ longi+">");
                    url = ("https://www.google.com/maps/search/?api=1&query=" + lati + "," + longi);

                }
                UsersDB db = new UsersDB(getActivity());

                User user = db.getUser(Preferences.readString(getContext(), "username"));
                String getName = Preferences.readString(getActivity(), "username");
                //get data from arraylist onto a regular array
                for (User aUser : users) {
//            Log.d(TAG, aUser.getUsername());
                    mobileArrays.add(aUser.getPhone_number());
                    nameArrays.add(aUser.getName());

//            mobileArray.add("Phone: "+aUser.getPhone_number());
                }
                for (int i = 0; i < mobileArrays.size(); i++) {
                    SmsManager.getDefault().sendTextMessage("" + mobileArrays.get(i), null, "" + getName + " Needs your help, she might have problem, please click on the link below to find out about " + getName + "\n" + url, null, null);
                    Log.i("imam", "onClick: " + mobileArrays.get(i));

                }
                Toast.makeText(getActivity(), "Alert sent.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double loX = sensorEvent.values[0];
        double loY = sensorEvent.values[1];
        double loZ = sensorEvent.values[2];

        double loAccelerationReader = Math.sqrt(Math.pow(loX, 2) + Math.pow(loY, 2) + Math.pow(loZ, 2));

        DecimalFormat precision = new DecimalFormat("0.00");
        double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));
//        Log.i("imam", "onSensorChanged: check"+ldAccRound);

        if (ldAccRound > 0.3d && ldAccRound < 0.4d) {
            boolean check = true;
            if (MyApplication.isInForeground()) {
                //Do your stuff
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Fall alert");
                builder.setMessage("Are you in danger?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                final AlertDialog alert = builder.create();
                alert.show();
                new CountDownTimer(600000, 1000) {
                    @Override
                    public void onTick(long l) {
                        alert.setMessage("left: " + l);
                    }

                    @Override
                    public void onFinish() {
                        mobileArrays.clear();
                        String url = "";
                        UsersDB dbb = new UsersDB(getActivity());
                        users = dbb.getRelativeList();
                        if (users.isEmpty()) {
                            Toast.makeText(getActivity(), "Add emergency contacts", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Constants.location != null) {
//                     url =( "http://maps.google.com/maps?q=loc:" +"<"+Constants.location.getLatitude()+","+ Constants.location.getLongitude()+">");
                            url = ("https://www.google.com/maps/search/?api=1&query=" + Constants.location.getLatitude() + "," + Constants.location.getLongitude());

                        } else {
                            //To retrieve
                            SharedPreferences sharedPref = context.getSharedPreferences("location", 0);
                            String lat = sharedPref.getString("lat", "21"); //0 is the default value
                            String lng = sharedPref.getString("lng", "32"); //0 is the default value
                            Double lati = Double.valueOf(lat);
                            Double longi = Double.valueOf(lng);
//                    url =( "http://maps.google.com/maps?q=loc:" +"<"+lati+","+ longi+">");
                            url = ("https://www.google.com/maps/search/?api=1&query=" + lati + "," + longi);

                        }
                        String getName = Preferences.readString(getActivity(), "username");
                        for (User aUser : users) {
                            mobileArrays.add(aUser.getPhone_number());
                        }
                        for (int i = 0; i < mobileArrays.size(); i++) {
                            SmsManager.getDefault().sendTextMessage("" + mobileArrays.get(i), null, "" + getName + " Needs your help, she might have problem, please click on the link below to find out about " + getName + "\n" + url, null, null);
                            Log.i("imam", "onClick: " + mobileArrays.get(i));

                        }
                        Toast.makeText(getActivity(), "Alert sent.", Toast.LENGTH_SHORT).show();
                        alert.setMessage("Alert Sent");
                    }
                }.start();
            } else {
                Log.i("imam", "onSensorChanged: generated Fragment foreground" + ldAccRound);
                //Do your stuff
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Fall alert");
                builder.setMessage("Are you in danger?");
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                final AlertDialog alert = builder.create();
                alert.show();
                new CountDownTimer(600000, 1000) {
                    @Override
                    public void onTick(long l) {
                        alert.setMessage("left: " + l);
                    }

                    @Override
                    public void onFinish() {
                        mobileArrays.clear();
                        String url = "";
                        UsersDB dbb = new UsersDB(getActivity());
                        users = dbb.getRelativeList();
                        if (users.isEmpty()) {
                            Toast.makeText(getActivity(), "Add emergency contacts", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Constants.location != null) {
//                     url =( "http://maps.google.com/maps?q=loc:" +"<"+Constants.location.getLatitude()+","+ Constants.location.getLongitude()+">");
                            url = ("https://www.google.com/maps/search/?api=1&query=" + Constants.location.getLatitude() + "," + Constants.location.getLongitude());

                        } else {
                            //To retrieve
                            SharedPreferences sharedPref = context.getSharedPreferences("location", 0);
                            String lat = sharedPref.getString("lat", ""); //0 is the default value
                            String lng = sharedPref.getString("lng", ""); //0 is the default value
                            Double lati = Double.valueOf(lat);
                            Double longi = Double.valueOf(lng);
//                    url =( "http://maps.google.com/maps?q=loc:" +"<"+lati+","+ longi+">");
                            url = ("https://www.google.com/maps/search/?api=1&query=" + lati + "," + longi);

                        }
                        String getName = Preferences.readString(getActivity(), "username");
                        for (User aUser : users) {
                            mobileArrays.add(aUser.getPhone_number());
                        }
                        for (int i = 0; i < mobileArrays.size(); i++) {
                            SmsManager.getDefault().sendTextMessage("" + mobileArrays.get(i), null, "" + getName + " Needs your help, she might have problem, please click on the link below to find out about " + getName + "\n" + url, null, null);
                            Log.i("imam", "onClick: " + mobileArrays.get(i));
                        }
                        Toast.makeText(getActivity(), "Alert sent.", Toast.LENGTH_SHORT).show();
                        alert.setMessage("Alert Sent");
                    }
                }.start();
                Log.i("imam", "onSensorChanged: generated Fragment background" + ldAccRound);


            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensor.unregisterListener(this);

    }
}