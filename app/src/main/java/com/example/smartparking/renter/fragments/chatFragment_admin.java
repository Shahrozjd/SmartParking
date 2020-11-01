package com.example.smartparking.renter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class chatFragment_admin extends Fragment {

    String  mail;
    Button button;
    EditText editText;
    public ListAdapter adapter = null;
    private FirebaseAuth mauth;
    private FirebaseFirestore db;
    ListView listView;

    public chatFragment_admin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_admin, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = getActivity().findViewById(R.id.sender_admin);
        editText = getActivity().findViewById(R.id.senderText_admin);
        mauth = FirebaseAuth.getInstance();
        FirebaseUser renter = mauth.getCurrentUser();
        mail=renter.getEmail().toString();
        db=FirebaseFirestore.getInstance();
        listView=getActivity().findViewById(R.id.admin_chats);
        func();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString().trim();

                Map<String, Object> userdata = new HashMap<>();
                userdata.put("sender", mail);
                userdata.put("msg", msg);
                userdata.put("time",System.currentTimeMillis());

                String id = db.collection("adminchat").document(mail).collection("messages").document().getId();
                db.collection("adminchat").document(mail).collection("messages").document(id)
                        .set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editText.setText("");
                        Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
                        func();
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
    public void func(){
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db.collection("adminchat").document(mail).collection("messages").orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, String> item = new HashMap<>();
                                String sender, msg;
                                sender=document.get("sender").toString();
                                msg=document.get("msg").toString();
                                item.put("sender",sender);
                                item.put("msg",msg);
                                list.add(item);
                            }
                        } else {
                        }
                        adapter = new SimpleAdapter(getContext(),list,R.layout.messages,
                                new String[]{"msg","sender"},
                                new int[]{R.id.message_text,R.id.message_sender});
                        listView.setAdapter(adapter);
                    }

                });
    }


}