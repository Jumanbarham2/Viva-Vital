package com.example.vivavital;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class Progress_Tracker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracker);
        SimpleGraphView graph = findViewById(R.id.simpleGraph);
        List<Float> data = Arrays.asList(120f, 122f, 119f, 118f, 121f);
        graph.setData(data);

    }
}