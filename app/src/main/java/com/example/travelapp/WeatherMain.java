package com.example.travelapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherMain extends AppCompatActivity {
    // creates and displays the WeatherFragment in the specified container layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weather);

        getSupportFragmentManager().beginTransaction().add(R.id.weather_fragment_container, new WeatherFragment()).commit();
    } 
}
