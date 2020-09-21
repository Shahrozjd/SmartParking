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
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;


public class tenant_parking_details extends Fragment implements OnMapReadyCallback {


    GoogleMap gm;
    TextView title, address, rate, activedays, starttime, endtime,txtdistance;
    String id;
    String distance;
    double parklat;
    double parklon;
    private FirebaseFirestore db;

    public tenant_parking_details() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        id = getArguments().getString("id");
        distance = getArguments().getString("distance");
        return inflater.inflate(R.layout.fragment_tenant_parking_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        title = getActivity().findViewById(R.id.detailTitle);
        address = getActivity().findViewById(R.id.detailAddress);
        rate = getActivity().findViewById(R.id.detailRate);
        activedays = getActivity().findViewById(R.id.detailDays);
        starttime = getActivity().findViewById(R.id.detail_starttime);
        endtime = getActivity().findViewById(R.id.detail_endtime);
        txtdistance = getActivity().findViewById(R.id.detail_distance);

        final ProgressDialog loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
        loading.setCancelable(false);

        db = FirebaseFirestore.getInstance();


        final DocumentReference docRef = db.collection("parking").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        title.setText(document.getData().get("title").toString().trim());
                        address.setText(document.getData().get("address").toString().trim());
                        rate.setText(document.getData().get("rate").toString().trim());
                        activedays.setText(document.getData().get("activedays").toString().trim());
                        starttime.setText(document.getData().get("starttime").toString().trim());
                        endtime.setText(document.getData().get("endtime").toString().trim());
                        txtdistance.setText(distance);

                        parklat = Double.parseDouble(document.getData().get("Latitude").toString().trim());
                        parklon = Double.parseDouble(document.getData().get("Longitude").toString().trim());
                        Log.d("TAGP",parklat+" "+parklon);
                        loading.dismiss();

                    } else {
                        loading.dismiss();
                        Toast.makeText(getContext(), "No such document", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No such document");

                    }
                } else {
                    loading.dismiss();
                    Log.d(TAG, "get failed with ", task.getException());
                }


            }
        }
        );
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.tenant_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;


        gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                LatLng parkinglatlon = new LatLng(parklat,parklon);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(parkinglatlon);
                markerOptions.title("Parking Location");
                gm.clear();
                gm.animateCamera(CameraUpdateFactory.newLatLngZoom(parkinglatlon,18));
                gm.addMarker(markerOptions);

            }
        });

    }
}