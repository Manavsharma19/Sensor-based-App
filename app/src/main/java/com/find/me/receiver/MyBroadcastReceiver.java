package com.find.me.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.find.me.fragments.HomeFragment;

import java.util.ArrayList;

import com.find.me.mapData.Constants;
import com.find.me.model.User;

public class MyBroadcastReceiver extends BroadcastReceiver {
    ArrayList<String> mobileArrays = new ArrayList<String>();
    String url = "xyz";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("imam", "came " );

        String action = intent.getAction();
        switch(action) {
            case "do_something":
                doSomething();
                break;
        }
    }
    public void doSomething() {
        if (!HomeFragment.users.isEmpty()){
            for (User aUser : HomeFragment.users) {
                if (Constants.location != null) {
                    url = ("https://www.google.com/maps/search/?api=1&query=" + Constants.location.getLatitude() + "," + Constants.location.getLongitude());

                } else {
                    //To retrieve
      /*              SharedPreferences sharedPref = context.getSharedPreferences("location", 0);

                    String lat = Pref.getString("lat", ""); //0 is the default value
                    String lng = sharedPref.getString("lng", ""); //0 is the default value
                    Double lati = Double.valueOf(lat);
                    Double longi = Double.valueOf(lng);
//                    url =( "http://maps.google.com/maps?q=loc:" +"<"+lati+","+ longi+">");
                    url = ("https://www.google.com/maps/search/?api=1&query=" + lati + "," + longi);*/

                }

//            Log.d(TAG, aUser.getUsername());
                mobileArrays.add(aUser.getPhone_number());
//            mobileArray.add("Phone: "+aUser.getPhone_number());
            }
            for (int i = 0; i < mobileArrays.size(); i++) {
//                SmsManager.getDefault().sendTextMessage("" + mobileArrays.get(i), null, ""  + " Needs your help, she might have problem, please click on the link below to find out about " +   "\n" + url, null, null);
                Log.i("imam", "onClick: " + mobileArrays.get(i));

            }
//            Toast.makeText(getActivity(), "Alert sent.", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("imam", "onClick: else " );

        }
        //Whatever you wanna do on notification click
    }
}