package com.example.smartparking.renter.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class home_renter extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;
    public ListAdapter adapter = null;
    public ListView listView;
    public home_renter() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_renter, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
        loading.setCancelable(false);

        floatingActionButton = getActivity().findViewById(R.id.fab);
        listView = getActivity().findViewById(R.id.list_renter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment;
                fragment = new AddPost();
                loadFragment(fragment);

            }
        });


        //fetch parking data
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String mail=currentUser.getEmail().toString();

        final ArrayList<HashMap<String, String>> list = new ArrayList<>();

        db.collection("parking")
                .whereEqualTo("email", mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Log.d("TAGH", document.getId() + " => " + document.getData());

                                String Title = document.getData().get("title").toString().trim();
                                String address = document.getData().get("address").toString().trim();
                                String rate = document.getData().get("rate").toString().trim();
                                String activedays = document.getData().get("activedays").toString().trim();
                                String starttime = document.getData().get("starttime").toString().trim();
                                String endtime = document.getData().get("endtime").toString().trim();
                                String Latitiude = document.getData().get("Latitude").toString().trim();
                                String Longitude = document.getData().get("Longitude").toString().trim();
                                String email = document.getData().get("email").toString().trim();
                                String id = document.getData().get("id").toString().trim();
                                String available = document.getData().get("available").toString().trim();

                                HashMap<String, String> item = new HashMap<>();

                                item.put("title", Title);
                                item.put("address", address);
                                item.put("rate", rate+" $/hr");
                                item.put("activedays", activedays);
                                item.put("starttime", starttime);
                                item.put("endtime", endtime);
                                item.put("Latitude", Latitiude);
                                item.put("Longitude", Longitude);
                                item.put("email", email);
                                item.put("id",id);
                                item.put("available",available);



                                list.add(item);



                            }
                        } else {
                            loading.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        adapter = new SimpleAdapter(getContext(),list,R.layout.renter_ads_card,
                                new String[]{"title","rate","address","id"},
                                new int[]{R.id.pktitle,R.id.pkrate,R.id.pkaddress,R.id.pkid});


                        listView.setAdapter(adapter);
                        loading.dismiss();
                    }
                });


        //sending data to nexr fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);



                Fragment fragment;
                fragment = new renter_single_parking();

                Bundle args = new Bundle();
                args.putString("id",item.get("id"));

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
                    .replace(R.id.fragment_container_renter, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    //bitmap to string
//    public Bitmap StringToBitMap(String encodedString){
//        try {
//            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
//            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch(Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
}