package com.example.smartparking.tenant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartparking.R;
import com.example.smartparking.tenant.fragments.home_tenant;
import com.example.smartparking.tenant.fragments.other_tenant;
import com.example.smartparking.tenant.fragments.profile_tenant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TenantMain extends AppCompatActivity {


    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_main);

        bottomNavigationView = findViewById(R.id.navigationView);

        //default fragment
        loadFragment(new home_tenant());

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment fragment = null;

                        switch (item.getItemId()) {
                            case R.id.nav_home_tenant:
                                fragment = new home_tenant();
                                break;
                            case R.id.nav_profile_tenant:
                                fragment = new profile_tenant();
                                break;
                            case R.id.nav_other_tenant:
                                fragment = new other_tenant();
                                break;
                        }
                        return loadFragment(fragment);
                    }
                });

    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_tenant, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {


            if (doubleBackToExitPressedOnce) {

                super.onBackPressed();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(a);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        else {

            getSupportFragmentManager().popBackStack();


        }
    }


}