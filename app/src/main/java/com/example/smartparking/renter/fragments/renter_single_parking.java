package com.example.smartparking.renter.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.ContentValues.TAG;

public class renter_single_parking extends Fragment {

    String ParkingID;
    private FirebaseFirestore db;
    ImageView imageView;

    public renter_single_parking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ParkingID = getArguments().getString("id");
        return inflater.inflate(R.layout.fragment_renter_single_parking, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(getContext(), ParkingID, Toast.LENGTH_SHORT).show();

        imageView = getActivity().findViewById(R.id.singleimg);

        final ProgressDialog loading = ProgressDialog.show(getContext(), "Fetching Profile", "Please wait ...");
        loading.setCancelable(false);

        db = FirebaseFirestore.getInstance();



        DocumentReference docRef = db.collection("parking").document(ParkingID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        loading.dismiss();
                        Toast.makeText(getContext(), document.getData().toString(), Toast.LENGTH_SHORT).show();


                        String data = document.getData().get("available").toString()+
                                ","+ document.getData().get("id").toString();

                        GeneratQR(data);

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
        });


    }


    private String GeneratQR(String data)
    {
        String getQr = null;
        Bitmap bitmap;
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 1000);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
//            // Setting Bitmap to ImageView
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("TAG", e.toString());
        }

        return getQr;
    }
}