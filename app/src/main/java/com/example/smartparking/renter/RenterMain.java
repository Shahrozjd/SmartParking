package com.example.smartparking.renter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.smartparking.R;
import com.example.smartparking.renter.fragments.chatFragment_renter;
import com.example.smartparking.renter.fragments.home_renter;
import com.example.smartparking.renter.fragments.other_renter;
import com.example.smartparking.renter.fragments.profile_renter;
import com.example.smartparking.tenant.fragments.home_tenant;
import com.example.smartparking.tenant.fragments.other_tenant;
import com.example.smartparking.tenant.fragments.profile_tenant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RenterMain extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_main);

        bottomNavigationView=findViewById(R.id.renter_navigationView);

        loadFragment(new home_renter());

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment fragment = null;

                        switch (item.getItemId()) {
                            case R.id.nav_home_renter:
                                fragment = new home_renter();
                                break;
                            case R.id.nav_profile_renter:
                                fragment = new profile_renter();
                                break;
                            case R.id.nav_other_renter:
                                fragment = new chatFragment_renter();
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
                    .replace(R.id.fragment_container_renter, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}