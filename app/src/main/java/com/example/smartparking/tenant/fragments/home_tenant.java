package com.example.smartparking.tenant.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class home_tenant extends Fragment {

    ImageView getNearbyLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    double tenantLat;
    double tenantLon;


    public home_tenant() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_tenant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNearbyLocation = getActivity().findViewById(R.id.btngetNearby);

        //get tenant current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getNearbyLocation.setOnClickListener(new View.OnClickListener() {
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

    }

    private void getlocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if(location != null)
                {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );

                        Log.d("TAGL",addresses.get(0).getLatitude()+" "+addresses.get(0).getLongitude());

                        tenantLat = addresses.get(0).getLatitude();
                        tenantLon = addresses.get(0).getLongitude();

                        getParkingDistance();

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void getParkingDistance() {
        //get all parkings data
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail=currentUser.getEmail().toString();
        db.collection("parking")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                                Toast.makeText(getContext(), document.getData().toString(), Toast.LENGTH_SHORT).show();

                                double renterLat = Double.parseDouble(document.getData().get("Latitude").toString());
                                double renterLon = Double.parseDouble(document.getData().get("Longitude").toString());


                                if ((tenantLat == renterLat) && (tenantLon == renterLon)) {

                                }
                                else
                                {
                                    double theta = tenantLon - renterLon;
                                    double dist = Math.sin(Math.toRadians(tenantLat)) * Math.sin(Math.toRadians(renterLat))
                                            + Math.cos(Math.toRadians(tenantLat)) * Math.cos(Math.toRadians(renterLat))
                                            * Math.cos(Math.toRadians(theta));
                                    dist = Math.acos(dist);
                                    dist = Math.toDegrees(dist);
                                    dist = dist * 60 * 1.1515* 1.609344;

                                    Toast.makeText(getContext(), String.valueOf(new DecimalFormat("##.##").format(dist)), Toast.LENGTH_SHORT).show();

                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}