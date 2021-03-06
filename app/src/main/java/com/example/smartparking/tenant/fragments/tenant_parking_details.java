package com.example.smartparking.tenant.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.content.ContentValues.TAG;


public class tenant_parking_details extends Fragment implements OnMapReadyCallback {


    Button scanqr, chat;
    GoogleMap gm;
    TextView title, address, rate, activedays, starttime, endtime,txtdistance;
    String id, renterid;
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


        scanqr = getActivity().findViewById(R.id.tenant_scanqr);
        chat = getActivity().findViewById(R.id.tenant_chat);
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
                        renterid=document.getData().get("email").toString().trim();
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

        //SCANNING QR
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 198);
                }
                else
                {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(tenant_parking_details.this);
                    integrator.setPrompt("Scan a QR");
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setOrientationLocked(true);
                    integrator.setBeepEnabled(true);
                    integrator.setCaptureActivity(CaptureActivityPortrait.class);
                    integrator.initiateScan();
                }


            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new chatFragment_tenant();

                Bundle args = new Bundle();
                args.putString("renterid", renterid);

                fragment.setArguments(args);

                loadFragment(fragment);
            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_tenant, fragment)
                    .commit();
            return true;
        }
        return false;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                if(result.getContents().trim().length()>3)
                {
                    String str = result.getContents().trim().substring(0,3);
                    if(str.equals("s#p"))
                    {

                        Fragment fragment;
                        fragment = new startparking();
                        Bundle args = new Bundle();
                        args.putString("qrdata",result.getContents().trim());
                        fragment.setArguments(args);
                        loadFragment(fragment);



                    }
                    else
                    {
                        Toast.makeText(getContext(), "Please use certified QR", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Please use certified QR", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
