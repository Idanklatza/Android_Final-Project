package com.example.travelapp;

import com.google.gson.annotations.SerializedName;
import com.example.travelapp.model.Weather;

// weather of type Weather. The @SerializedName annotation is used to map the JSON key "main" to the weather field when converting JSON to Java objects
public class WeatherSearchResult {
    @SerializedName("main")
    Weather weather;

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
