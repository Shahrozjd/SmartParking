package com.example.smartparking.tenant.fragments;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.smartparking.R;
import com.example.smartparking.renter.fragments.renter_single_parking;
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
import com.google.firestore.v1.TargetOrBuilder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class home_tenant extends Fragment {

    Button getNearbyLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public ListAdapter adapter = null;
    public ListView listView;
    double tenantLat;
    double tenantLon;
    ProgressDialog loading;

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
        listView = getActivity().findViewById(R.id.tenant_home_list);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        if (ActivityCompat.checkSelfPermission(getContext()
//                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //when permission granted
//            loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
//            loading.setCancelable(false);
//            getlocation();
//        } else {
//            //when permission denied
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//
//
//        }

        //get tenant current location
        getNearbyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //when permission granted
                    loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
                    loading.setCancelable(false);
                    getlocation();
                } else {
                    //when permission denied
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);


                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);

                Toast.makeText(getContext(), item.get("id")+item.get("distance"), Toast.LENGTH_SHORT).show();

                Fragment fragment;
                fragment = new tenant_parking_details();

                Bundle args = new Bundle();
                args.putString("id",item.get("id"));
                args.putString("distance",item.get("distance"));

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

        final ArrayList<HashMap<String, String>> list = new ArrayList<>();

        db.collection("parking")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


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
                                    double parkingDistance = Double.parseDouble(new DecimalFormat("##.##").format(dist));

                                    HashMap<String, String> item = new HashMap<>();
                                    String availability = "";
                                    String pkdistance = String.valueOf(parkingDistance)+"KM";
                                    String rate = document.getData().get("rate").toString().trim()+"A$";
                                    item.put("parkingDistance", String.valueOf(parkingDistance));
                                    item.put("id",document.getData().get("id").toString());
                                    item.put("title",document.getData().get("title").toString());
                                    item.put("rate",rate);
                                    item.put("address",document.getData().get("address").toString());
                                    item.put("distance",pkdistance);

                                    if(document.getData().get("available").toString().trim().equals("true"))
                                    {
                                        availability = "Available";
                                    }
                                    else
                                    {
                                        availability = "Occupied";
                                    }


                                    item.put("available",availability);
                                    list.add(item);

                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        final ArrayList<HashMap<String, String>> finallist = new ArrayList<>();
                        int size = list.size();
                        for(int i=0;i<size;i++)
                        {
                            double distanceThreshold = 500.0;
                            if (Double.parseDouble(list.get(i).get("parkingDistance")) <= distanceThreshold) {

                                finallist.add(list.get(i));
                            }
                        }

                        Log.d("TAG",finallist.toString());

                        adapter = new SimpleAdapter(getContext(),finallist,R.layout.tenant_parking_crads,
                                new String[]{"title","rate","address","id","available","distance"},
                                new int[]{R.id.tenant_pk_title,R.id.tenant_pk_rate
                                        ,R.id.tenant_address,R.id.tenant_pk_id,R.id.tenant_pk_available,R.id.tenant_pk_distance});

                        listView.setAdapter(adapter);

                        loading.dismiss();

                    }
                });
    }
}