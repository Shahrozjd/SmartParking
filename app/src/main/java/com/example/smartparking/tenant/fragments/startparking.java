package com.example.smartparking.tenant.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class startparking extends Fragment {


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
        db = FirebaseFirestore.getInstance();

        values = qrdata.split(",");

        availability = values[1];
        parkid = values[2];
        Toast.makeText(getContext(), "check : " + availability+"/ id : "+parkid, Toast.LENGTH_LONG).show();

//        updateavailabilty();
    }

    private void updateavailabilty() {

        final ProgressDialog loading = ProgressDialog.show(getContext(), "Saving Data", "Please wait ...");
        loading.setCancelable(false);

        Map<String, Object> userdata = new HashMap<>();
        userdata.put("available", "".toString());

        DocumentReference docRef = db.collection("renter").document("mail");
        docRef
                .update(userdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loading.dismiss();
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
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


}