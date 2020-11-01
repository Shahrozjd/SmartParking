package com.example.smartparking.renter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.R;
import com.example.smartparking.tenant.LoginActivity_tenant;
import com.example.smartparking.tenant.TenantMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity_renter extends AppCompatActivity {

    EditText email_txt;
    EditText password_txt;
    Button signin_btn;
    private FirebaseAuth mauth;
    TextView signupscreen;

    @Override
    protected void onStart() {
        super.onStart();
        mauth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser == null)
        {
            Log.d("TAGU","No User");
        }
        else
        {
            Log.d("TAGU",currentUser.getEmail().toString());

            Intent i = new Intent(LoginActivity_renter.this, RenterMain.class);
            startActivity(i);
            Toast.makeText(this, "WELCOME BACK!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        signupscreen = findViewById(R.id.txtsignupscreen);
        signupscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity_renter.this,SignupActivity_renter.class);
                startActivity(i);

            }
        });
        email_txt = findViewById(R.id.txtemail_login);
        password_txt = findViewById(R.id.txtpassword_login);
        signin_btn = findViewById(R.id.btnlogin);


        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInrenter();
//                Intent i = new Intent(LoginActivity_renter.this, RenterMain.class);
//                startActivity(i);
            }
        });

    }
    private void LogInrenter() {

        final ProgressDialog loading = ProgressDialog.show(this, "Signing In", "Please wait ...");
        loading.setCancelable(false);

        String email = email_txt.getText().toString().trim();
        String password = password_txt.getText().toString().trim();

        mauth = FirebaseAuth.getInstance();

        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mauth.getCurrentUser();
                    Intent i = new Intent(LoginActivity_renter.this, RenterMain.class);
                    startActivity(i);
                    loading.dismiss();
                } else {

                    loading.dismiss();
                    Toast.makeText(LoginActivity_renter.this, "SignIn failed.", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

}