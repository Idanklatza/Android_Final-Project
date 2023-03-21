package com.example.travelapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.travelapp.model.Weather;
import com.example.travelapp.model.WeatherApi;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment {

    EditText et;
    TextView tv;
    ImageView img;
    String url = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String apikey = "0bcdbcf07fe2ed0f790d1dfbc0598da2";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        et = root.findViewById(R.id.et);
        tv = root.findViewById(R.id.tv);
        img = root.findViewById(R.id.fragment_weather_tmp_img);
        img.setVisibility(View.INVISIBLE);

        MaterialButton button = root.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getweather(v);
            }
        });
        return root;
    }

    // getweather method creates a retrofit instance with the base URL for the OpenWeatherMap API and return the weather by city name
    public void getweather(View v) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/").addConverterFactory(GsonConverterFactory.create()).build();
        WeatherApi myapi = retrofit.create(WeatherApi.class);
        Call<WeatherSearchResult> examplecall = myapi.getweather(et.getText().toString().trim(), apikey);
        examplecall.enqueue(new Callback<WeatherSearchResult>() {
            @Override
            public void onResponse(Call<WeatherSearchResult> call, Response<WeatherSearchResult> response) {
                if (response.isSuccessful()) {
                    WeatherSearchResult mydata = response.body();
                    if (mydata != null) {
                        Weather weather = mydata.getWeather();
                        Double temp = weather.getTemp();
                        Integer humidity = weather.getHumidity();
                        Integer temperature = (int) (temp - 273.15);
                        tv.setText("Temperature: " + String.valueOf(temperature) + "C\n");
                        tv.append("Humidity: " + String.valueOf(humidity) + "%");
                        // replace the image according to the temperature of that city
                        if(Integer.valueOf(temperature)>25) {
                            Picasso.get().load("android.resource://com.example.travelapp/" + R.drawable.ic_clear_day).into(img);
                            img.setVisibility(View.VISIBLE);
                        } if(Integer.valueOf(temperature)<=25 && Integer.valueOf(temperature)>=20) {
                            Picasso.get().load("android.resource://com.example.travelapp/" + R.drawable.ic_few_clouds).into(img);
                            img.setVisibility(View.VISIBLE);
                        } if(Integer.valueOf(temperature)<20) {
                            Picasso.get().load("android.resource://com.example.travelapp/" + R.drawable.ic_cloudy_weather).into(img);
                            img.setVisibility(View.VISIBLE);
                        }
                    } else { // empty value or invalid city
                        Toast.makeText(getContext(), "Invalid city name", Toast.LENGTH_LONG).show();
                        Picasso.get().load("android.resource://com.example.travelapp/" + R.drawable.no_city).into(img);
                        img.setVisibility(View.VISIBLE);
                    }
                } else { // mydata == null
                    tv.setText("city does not exist");
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    Picasso.get().load("android.resource://com.example.travelapp/" + R.drawable.no_city).into(img);
                    img.setVisibility(View.VISIBLE);
                }
            }
            // communication error, crash, etc
            @Override
            public void onFailure(Call<WeatherSearchResult> call, Throwable t) {
                Log.d("TAG", "searchWeatherByCity fail");
                img.setVisibility(View.INVISIBLE);
            }

        });
    }
}
