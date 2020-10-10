package com.example.smartparking.tenant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class chatFragment_tenant  extends Fragment {

    String renterid;
    Button button;
    EditText editText;
    private FirebaseAuth mauth;
    private FirebaseFirestore db;

    public chatFragment_tenant() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        renterid = getArguments().getString("renterid");
        return inflater.inflate(R.layout.fragment_chat_tenant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = getActivity().findViewById(R.id.sender_tenant);
        editText = getActivity().findViewById(R.id.senderText_tenant);
        mauth = FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString().trim();
                FirebaseUser tenant = mauth.getCurrentUser();
                String mail=tenant.getEmail().toString();
                Map<String, Object> userdata = new HashMap<>();
                userdata.put("sender", mail);
                userdata.put("msg", msg);
                userdata.put("time",System.currentTimeMillis());
                db= FirebaseFirestore.getInstance();
                String id = db.collection("chats").document("chat_"+renterid+"_"+mail).collection("messages").document().getId();
                db.collection("chats").document("chat_"+renterid+"_"+mail).collection("messages").document(id)
                    .set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
                    }
                })
                    .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Message sending Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
