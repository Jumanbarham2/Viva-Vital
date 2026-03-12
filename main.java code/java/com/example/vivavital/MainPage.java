package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainPage extends AppCompatActivity {
private ImageButton medication, fitness, wellness,activity_tracker,progress_tracker,health_status, recipes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        medication =findViewById(R.id.medication);
        fitness =findViewById(R.id.fitness);
        wellness =findViewById(R.id.wellness);
        activity_tracker =findViewById(R.id.activity);
        progress_tracker =findViewById(R.id.progress);
        health_status =findViewById(R.id.status);
        recipes =findViewById(R.id.recipes);
        medication.setOnClickListener(v -> {
        startActivity(new Intent(MainPage.this,Medication.class));

        });
        fitness.setOnClickListener(v -> {
            startActivity(new Intent(MainPage.this,Fitness.class));

        });
        recipes.setOnClickListener(v -> {
            startActivity(new Intent(MainPage.this,Healthy_Recipes.class));

        });
        wellness.setOnClickListener(v -> {
            startActivity(new Intent(MainPage.this,Wellness_Center.class));

        });
        health_status.setOnClickListener(v -> {
            startActivity(new Intent(MainPage.this,Health_Status.class));

        });
        progress_tracker.setOnClickListener(v -> {
            startActivity(new Intent(MainPage.this,Progress_Tracker.class));

        });
        activity_tracker.setOnClickListener(v -> {
        startActivity(new Intent(MainPage.this,Activity_Tracker.class));

        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

    ImageButton menuButton=findViewById(R.id.menu_button);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

                navigationView.setNavigationItemSelectedListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.nav_about) {
                        startActivity(new Intent(MainPage.this, AboutUs.class));
                    } else if (id == R.id.nav_settings) {
                        startActivity(new Intent(MainPage.this,Settings.class));
                    } else if (id == R.id.nav_logout) {
                        startActivity(new Intent(MainPage.this,LoginPage.class));                    }

                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });
            }
        }

