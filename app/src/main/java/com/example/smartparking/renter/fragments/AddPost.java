package com.example.smartparking.renter.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.smartparking.R;
import com.example.smartparking.renter.SignupActivity_renter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddPost extends Fragment implements CompoundButton.OnCheckedChangeListener {


    ToggleButton tM, tT, tW, tTh, tF, tSat, tSun;
    String markedButtons = "";
    TextView starttime, endtime, addLong, addLat;
    EditText addTitle, addAddress, addRate;
    Button getlocation, addbtn;
    FusedLocationProviderClient fusedLocationProviderClient;
    ProgressDialog loading;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public AddPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //Toggles
        tM = getActivity().findViewById(R.id.tM);
        tT = getActivity().findViewById(R.id.tT);
        tW = getActivity().findViewById(R.id.tW);
        tTh = getActivity().findViewById(R.id.tTh);
        tF = getActivity().findViewById(R.id.tF);
        tSat = getActivity().findViewById(R.id.tSat);
        tSun = getActivity().findViewById(R.id.tSun);

        //Buttons
        getlocation = getActivity().findViewById(R.id.btngetloc);
        addbtn = getActivity().findViewById(R.id.btnaddParking);

        //Textviews
        starttime = getActivity().findViewById(R.id.addstartTime);
        endtime = getActivity().findViewById(R.id.addEndTime);
        addLat = getActivity().findViewById(R.id.addLat);
        addLong = getActivity().findViewById(R.id.addLong);

        //EditText
        addTitle = getActivity().findViewById(R.id.addtitle);
        addAddress = getActivity().findViewById(R.id.addaddress);
        addRate = getActivity().findViewById(R.id.addRate);

        //Toggle Click listener
        tM.setOnCheckedChangeListener(this);
        tT.setOnCheckedChangeListener(this);
        tW.setOnCheckedChangeListener(this);
        tTh.setOnCheckedChangeListener(this);
        tF.setOnCheckedChangeListener(this);
        tSat.setOnCheckedChangeListener(this);
        tSun.setOnCheckedChangeListener(this);

        //Set Start Time
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        starttime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });

        //Set End Time
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endtime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        //Get User Current Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //when permission granted
                    getlocation();
                } else {
                    //when permission denied
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);


                }

            }
        });

        //Adding All data to Cloud Firestore


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog loading = ProgressDialog.show(getContext(), "Saving Data", "Please wait ...");
                loading.setCancelable(false);

                //fetching aut email and intitliazing cloud instance
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String mail = currentUser.getEmail();

                String Title = addTitle.getText().toString().trim();
                String Address = addAddress.getText().toString().trim();
                String Rate = addRate.getText().toString().trim();
                String activedays = markedButtons;
                String startTime = starttime.getText().toString().trim();
                String endTime = endtime.getText().toString().trim();
                String Lat = addLat.getText().toString().trim();
                String Lon = addLong.getText().toString();
                String id = db.collection("parking").document().getId();

                Map<String, Object> userdata = new HashMap<>();
                userdata.put("id", id);
                userdata.put("email", mail);
                userdata.put("title", Title);
                userdata.put("address", Address);
                userdata.put("rate", Rate);
                userdata.put("activedays", activedays);
                userdata.put("starttime", startTime);
                userdata.put("endtime", endTime);
                userdata.put("Latitude", Lat);
                userdata.put("Longitude", Lon);
                userdata.put("available", "true");

                db.collection("parking").document(id)
                        .set(userdata)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                loading.dismiss();
                                Toast.makeText(getContext(), "Data successfully written!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getContext(), "Task Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });


    }

    private void getlocation() {


        final ProgressDialog loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
        loading.setCancelable(false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        String Lat = String.valueOf(addresses.get(0).getLatitude());
                        String Lon = String.valueOf(addresses.get(0).getLongitude());
                        addLat.setText(Lat);
                        addLong.setText(Lon);
                        loading.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


        switch (compoundButton.getId()) {
            case R.id.tM:
                if (b) {
                    markedButtons += "Monday,";
                } else {
                    markedButtons = markedButtons.replace("Monday,", "");
                }
                break;
            case R.id.tT:
                if (b) {
                    markedButtons += "Tuesday,";
                } else {
                    markedButtons = markedButtons.replace("Tuesday,", "");
                }
                break;
            case R.id.tW:
                if (b) {
                    markedButtons += "Wednesday,";
                } else {
                    markedButtons = markedButtons.replace("Wednesday,", "");
                }
                break;
            case R.id.tTh:
                if (b) {
                    markedButtons += "Thursday,";
                } else {
                    markedButtons = markedButtons.replace("Thursday,", "");
                }
                break;
            case R.id.tF:
                if (b) {
                    markedButtons += "Friday,";
                } else {
                    markedButtons = markedButtons.replace("Friday,", "");
                }
                break;
            case R.id.tSat:
                if (b) {
                    markedButtons += "Saturday,";
                } else {
                    markedButtons = markedButtons.replace("Saturday,", "");
                }
                break;
            case R.id.tSun:
                if (b) {
                    markedButtons += "Sunday,";
                } else {
                    markedButtons = markedButtons.replace("Sunday,", "");
                }
                break;
        }

        Toast.makeText(getContext(), markedButtons, Toast.LENGTH_SHORT).show();

    }






}