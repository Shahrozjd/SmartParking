package com.example.smartparking.tenant.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class startparking extends Fragment {

    TextView parkingstatus;
    TextView parkingresult;
    ImageButton btnstop;

    private FirebaseFirestore db;
    String qrdata;
    String availability;
    String parkid;
    public String[] values = null;
    public startparking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        qrdata = getArguments().getString("qrdata");
        return inflater.inflate(R.layout.fragment_startparking, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parkingstatus = getActivity().findViewById(R.id.parkingstatus);
        parkingresult = getActivity().findViewById(R.id.parkingresult);
        btnstop = getActivity().findViewById(R.id.btnStop);

        db = FirebaseFirestore.getInstance();

        values = qrdata.split(",");
        availability = values[1];
        parkid = values[2];

        Toast.makeText(getContext(),"******" + availability, Toast.LENGTH_SHORT).show();
        checkavailabilty();

        //STOP BUTTON
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                parkingstatus.setText("Parking time has been stopped");
                parkingstatus.setTextColor(Color.RED);
                parkingresult.setText("Your total is 5 A$ \n toal time of parking is 1 hour");
                parkingresult.setVisibility(View.VISIBLE);

                //Calculate hours

                SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm:ss aa");
                Date now = new Date();
                String strDate = sdfDate.format(now);
                Toast.makeText(getContext(), strDate.toString(), Toast.LENGTH_SHORT).show();

                checktrue();

            }
        });
    }


    private void checkavailabilty() {



        Map<String, Object> userdata = new HashMap<>();
        if(availability.equals("true"))
        {
            userdata.put("available", "false");
            Toast.makeText(getContext(), "Available", Toast.LENGTH_SHORT).show();
            startParkingProcess(userdata);
        }
        else
        {
            Toast.makeText(getContext(), "This parking is occupied", Toast.LENGTH_SHORT).show();
        }



    }

    private void startParkingProcess(Map<String,Object> userdata)
    {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "Update Info", "Please wait ...");
        loading.setCancelable(false);

        db.collection("parking").document(parkid)
                .update(userdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loading.dismiss();
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        //Status Active
                        parkingstatus.setText("Your parking time has been started");
                        parkingstatus.setTextColor(getResources().getColor(R.color.mygreen));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(getContext(), "Error updating document", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }
    private void checktrue() {

        Map<String, Object> userdata = new HashMap<>();
        userdata.put("available", "true");
        db.collection("parking").document(parkid)
                .update(userdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error updating document", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }




}

