package com.example.zakir.weather;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabOneFragment extends Fragment {
    private TextView  location, temp, description, date, maxTemp, minTemp, sunSet, sunRise, humedity, pressure, wind;
    private ImageView image;

    private CurrentWeatherService service;
    private CurrentWeatherResponse currentWeatherResponse;


    public TabOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_one, container, false);

        location = view.findViewById(R.id.myLocation);
        temp = view.findViewById(R.id.temp);
        image = view.findViewById(R.id.weatherImage);
        description = view.findViewById(R.id.description);
        date = view.findViewById(R.id.date);
        maxTemp = view.findViewById(R.id.maxTemp);
        minTemp = view.findViewById(R.id.minTemp);
        sunRise = view.findViewById(R.id.sunRise);
        sunSet = view.findViewById(R.id.sunSet);
        humedity = view.findViewById(R.id.humedity);
        pressure = view.findViewById(R.id.pressure);
        wind = view.findViewById(R.id.wind);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CurrentWeatherService.class);
        String endUrl = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",
                MainActivity.latitude,
                MainActivity.longitude,
                MainActivity.units,
                getString(R.string.weather_api_key));
        Call<CurrentWeatherResponse> call = service.getCurrentWeatherData(endUrl);
        call.enqueue(new Callback<CurrentWeatherResponse>() {

            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if(response.code() == 200){
                    currentWeatherResponse = response.body();
                    String loc = currentWeatherResponse.getName();
                    String country = currentWeatherResponse.getSys().getCountry();
                    location.setText(loc+", "+country);

                    double tmp = currentWeatherResponse.getMain().getTemp();
                    int mm = (int) tmp;
                    String nn = String.valueOf(mm);
                    temp.setText(nn+MainActivity.tempSign);

                    String iconString = currentWeatherResponse.getWeather().get(0).getIcon();
                    Uri iconUri = Uri.parse("http://openweathermap.org/img/w/"+iconString+".png");
                    Picasso.with(getActivity().getApplicationContext()).load(iconUri).into(image);

                    String desc = currentWeatherResponse.getWeather().get(0).getDescription().toString();
                    description.setText(desc);

                    long currentTime = currentWeatherResponse.getDt();
                    Date cTimeDateFormte = new Date(currentTime*1000L);
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
                    SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
                    String finalDate = df.format(cTimeDateFormte.getTime());
                    String curTime = dfTime.format(cTimeDateFormte.getTime());
                    date.setText(finalDate+"\n"+curTime);

                    String minTmp = currentWeatherResponse.getMain().getTempMin().toString();
                    minTemp.setText(minTmp+MainActivity.tempSign);

                    String maxTmp = currentWeatherResponse.getMain().getTempMax().toString();
                    maxTemp.setText(maxTmp+MainActivity.tempSign);

                    long unix_sunrise = currentWeatherResponse.getSys().getSunrise();
                    Date date_sunrise = new Date(unix_sunrise*1000L);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a");
                    String sunRs = df2.format(date_sunrise.getTime());
                    sunRise.setText(sunRs);

                    long unix_sunset = currentWeatherResponse.getSys().getSunset();
                    Date date_sunset = new Date(unix_sunset*1000L);
                    SimpleDateFormat df3 = new SimpleDateFormat("hh:mm a");
                    String sunSt = df3.format(date_sunset.getTime());
                    sunSet.setText(sunSt);

                    String hmedity = currentWeatherResponse.getMain().getHumidity().toString();
                    humedity.setText(hmedity+"%");

                    String prssure = currentWeatherResponse.getMain().getHumidity().toString();
                    pressure.setText(prssure+" mb");

                    String wnd = currentWeatherResponse.getWind().getSpeed().toString();
                    wind.setText(wnd+" mphn");
                }

            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

            }
        });

        return view;
    }
}
