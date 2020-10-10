package com.example.smartparking.tenant.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.MapsActivity;
import com.example.smartparking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class other_tenant extends Fragment {

    ListView listView;
    ListAdapter adapter=null;
    private FirebaseAuth mauth;
    private FirebaseFirestore db;

    public other_tenant() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_tenant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = getActivity().findViewById(R.id.renters_list);
        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();
        getrenter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);

                Toast.makeText(getContext(), item.get("renters"), Toast.LENGTH_SHORT).show();

                Fragment fragment;
                fragment = new chatFragment_tenant();

                Bundle args = new Bundle();
                args.putString("renterid",item.get("renters"));

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

   private void getrenter(){
       final ArrayList<HashMap<String, String>> list = new ArrayList<>();

       db.collection("renter")
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               String renters=document.getData().get("email").toString();
                               HashMap<String, String> item = new HashMap<>();
                               item.put("renters",renters);
                               item.put("name",document.getData().get("fname").toString());
                               list.add(item);
                           }
                       } else {
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }



//

                       adapter = new SimpleAdapter(getContext(),list,R.layout.userlist_card,
                               new String[]{"name"},
                               new int[]{R.id.user_list_item});
                       listView.setAdapter(adapter);
                   }
               });
   }
   }

