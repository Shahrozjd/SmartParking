package com.example.smartparking.tenant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.example.smartparking.renter.LoginActivity_renter;
import com.example.smartparking.renter.RenterMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity_tenant extends AppCompatActivity {

    EditText emailedittxt;
    EditText passwordedittxt;
    Button singin;
    TextView signupscreen;


    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupscreen = findViewById(R.id.txtsignupscreen);
        signupscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity_tenant.this,SignupActivity_tenant.class);
                startActivity(i);

            }
        });


        emailedittxt = findViewById(R.id.txtemail_login);
        passwordedittxt = findViewById(R.id.txtpassword_login);
        singin = findViewById(R.id.btnlogin);


        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInTenant();
//                Intent i = new Intent(LoginActivity_tenant.this, TenantMain.class);
//                startActivity(i);
            }
        });

    }

    private void SignInTenant() {

        final ProgressDialog loading = ProgressDialog.show(this, "Signing In", "Please wait ...");
        loading.setCancelable(false);
        String email = emailedittxt.getText().toString().trim();
        String password = passwordedittxt.getText().toString().trim();

        mauth = FirebaseAuth.getInstance();
        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mauth.getCurrentUser();
                    Intent i = new Intent(LoginActivity_tenant.this,TenantMain.class);
                    startActivity(i);
                    finish();
                    loading.dismiss();
                    Toast.makeText(LoginActivity_tenant.this, "SignIn Succesful.", Toast.LENGTH_SHORT).show();
                } else {

                    loading.dismiss();
                    Toast.makeText(LoginActivity_tenant.this, "SignIn failed.", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}