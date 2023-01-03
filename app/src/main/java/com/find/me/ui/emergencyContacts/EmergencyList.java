package com.find.me.ui.emergencyContacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.find.me.R;
import com.find.me.databinding.ActivityEmergencyListBinding;


public class EmergencyList extends AppCompatActivity {
    ActivityEmergencyListBinding binding;
    String connectionType = "";
    String number = "";
    private static final int REQUEST_CALL =11 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_emergency_list);
        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rgConnection.getCheckedRadioButtonId()== -1){
                    toast("Select Option");
                }
                else {
                    if(binding.rgCall.getCheckedRadioButtonId() == -1){
                        toast("Select Option");
                    }
                    else {
                        if (binding.egcall.isChecked()){
                            connectionType = "Emergency call";
//                            binding.linearLayout2.setVisibility(View.VISIBLE);

                        }
                        else {
                            connectionType = "Call by Phone";
//                            binding.linearLayout2.setVisibility(View.GONE);

                        }
                        if (binding.police.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }

                            number = "999";
                            makePhoneCall(Integer.parseInt(number));

                        }
                        else if (binding.ambulance.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }
                            number = "997";
                            makePhoneCall(Integer.parseInt(number));

                        }
                        else if (binding.civil.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }
                            number = "998";
                            makePhoneCall(Integer.parseInt(number));

                        }
                        else if (binding.traffic.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }
                            number = "993";
                            makePhoneCall(Integer.parseInt(number));

                        }
                        else if (binding.road.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }
                            number = "996";
                            makePhoneCall(Integer.parseInt(number));

                        }
                        else if (binding.najm.isChecked()){
                            if (connectionType.equals("Call by Phone")){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                startActivity(intent);
                                return;
                            }
                            number = "920000560";
                            makePhoneCall(Integer.parseInt(number));

                        }

                    }


                }

            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
    private void toast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void makePhoneCall(int number) {

        if (ContextCompat.checkSelfPermission(EmergencyList.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmergencyList.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(Integer.parseInt(number));
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}