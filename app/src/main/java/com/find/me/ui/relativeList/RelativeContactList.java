package com.find.me.ui.relativeList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import com.find.me.R;
import com.find.me.databinding.ActivityRelativeContactListBinding;
import com.find.me.dbHelper.UsersDB;
import com.find.me.model.User;
import com.find.me.utils.Preferences;

public class RelativeContactList extends AppCompatActivity {
    ActivityRelativeContactListBinding binding;
    TextView  yes, no;
    TextView number, namee, idnumberr;
    UsersDB db;
    String numbers = "";

    User user;
    private static final int REQUEST_CALL =11 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_relative_contact_list);
        ListView listView = (ListView) findViewById(R.id.mobile_list);

        UsersDB dbb = new UsersDB(this);

        //START LISTVIEW CODE
        ///////////////////////////////////////////
        ArrayList<String> mobileArray = new ArrayList<String>();

        //list of data to display
        ArrayList<User> users = dbb.getRelativeList();
        //get data from arraylist onto a regular array
        for (User aUser: users){
//            Log.d(TAG, aUser.getUsername());
            mobileArray.add(""+aUser.getName()+" ( "+aUser.getPhone_number()+" )");
//            mobileArray.add("Phone: "+aUser.getPhone_number());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mobileArray);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

//                Object o = listView.getItemAtPosition(position);
//                String getNo = listView.getSelectedItem().toString();
                String getNo = listView.getItemAtPosition(position).toString();
                getNo = getNo.split("[\\(\\)]")[1];
                getNo = getNo.replace(" ","");
                getNo = getNo.replace("+","");
                String getnn = getNo;
                numbers = getnn;
                Log.i("imam", "onItemSelected:"+getnn);
                makePhoneCall(numbers);
            }
        });
        binding.addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(RelativeContactList.this);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.bmi_info_dialog);
                yes = dialog.findViewById(R.id.yes);
                namee = dialog.findViewById(R.id.ETUsername);
                number = dialog.findViewById(R.id.pnumber);
                no = dialog.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pref = Preferences.readString(RelativeContactList.this,"username");
                        String rep = pref.replace("@","");
                        String rep1 = rep.replace(".","");

                        db = new UsersDB(RelativeContactList.this);

                        if(namee.getText().toString().equals("") || number.getText().toString().equals("")){
                            Toast.makeText(RelativeContactList.this, "fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            String getNo =  number.getText().toString();

                            String getName =  namee.getText().toString();
                            if(db.contactExists(getNo, rep1)) {
                                Toast.makeText(RelativeContactList.this, "already exists", Toast.LENGTH_SHORT).show();
                            }
                            else{//if the user does not exist, then it inserts it
//                                user = new User(getName, getNo,pref);
                                db.insertContact(getName, getNo,rep1);
                                Toast.makeText(RelativeContactList.this, "Successfully added ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                ArrayList<String> mobileArray = new ArrayList<String>();
                                //list of data to display
                                ArrayList<User> users = dbb.getRelativeList();
                                //get data from arraylist onto a regular array
                                for (User aUser: users){
                                    mobileArray.add(""+aUser.getName()+" ( "+aUser.getPhone_number()+" )");
                                }
                                ArrayAdapter adapter = new ArrayAdapter<String>(RelativeContactList.this, R.layout.activity_listview, mobileArray);

                                listView.setAdapter(adapter);
                            }
                        }


                        }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }
    private void makePhoneCall(String number) {

        if (ContextCompat.checkSelfPermission(RelativeContactList.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RelativeContactList.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" +number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(numbers);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}