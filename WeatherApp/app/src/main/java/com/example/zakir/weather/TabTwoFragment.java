package com.example.zakir.weather;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabTwoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CurrentWeatherService service;
    private ForcastWeatherResponse forcastWeatherResponse;
    private ArrayList<ForcastDetails> forcastDetailsArray = new ArrayList<>();
    private ForcastDetails forcastDetails;
    private ForcastAdapter forcastAdapter;
    private Calendar calendar;

    private String iconString, statusString, dayString, tempString, minTString, maxTString, sunRiseString, sunSetString;

    public TabTwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_two, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        calendar = Calendar.getInstance();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CurrentWeatherService.class);
        @SuppressLint("DefaultLocale") String endUrl = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",
                MainActivity.latitude,
                MainActivity.longitude,
                MainActivity.units,
                getString(R.string.weather_api_key));
        Call<ForcastWeatherResponse> call = service.getAllForcastData(endUrl);
        call.enqueue(new Callback<ForcastWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForcastWeatherResponse> call, @NonNull Response<ForcastWeatherResponse> response) {
                if(response.code() == 200){
                    forcastWeatherResponse = response.body();
                    ArrayList<ForcastDetails> details = new ArrayList<>();
                    for( int i = 0; i < forcastWeatherResponse.getList().size(); i++) {
                        iconString = forcastWeatherResponse.getList().get(i).getWeather().get(0).getIcon();

                        statusString = forcastWeatherResponse.getList().get(i).getWeather().get(0).getDescription();

                        long unix_day = forcastWeatherResponse.getList().get(i).getDt();
                        Date date = new Date(unix_day*1000L);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMd, hha");
                        SimpleDateFormat df2 = new SimpleDateFormat("hha");
                        String todayTime = df2.format(date.getTime());

                        SimpleDateFormat dfMy = new SimpleDateFormat("d");
                        int weatherDate = Integer.parseInt(dfMy.format(date.getTime()));
                        int sysDate = Integer.parseInt(dfMy.format(calendar.getTime()));
                        if( weatherDate == sysDate){
                            dayString = "Today, "+todayTime;
                        }
                        else if( (weatherDate-1) == sysDate){
                            dayString = "Tomorrow, "+todayTime;
                        }
                        else {
                            dayString = df.format(date.getTime());
                        }

                        tempString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTemp().intValue());

                        minTString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTempMin().intValue());

                        maxTString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTempMax().intValue());

                        sunRiseString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getHumidity().intValue());

                        sunSetString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getPressure().intValue());

                        forcastDetails = new ForcastDetails(iconString,statusString,dayString,tempString,minTString,maxTString,sunRiseString,sunSetString);
                        details.add(forcastDetails);
                    }
                    forcastDetailsArray = details;
                    forcastAdapter = new ForcastAdapter(getActivity().getApplicationContext(),forcastDetailsArray);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(llm);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(forcastAdapter);

                }
            }

            @Override
            public void onFailure(@NonNull Call<ForcastWeatherResponse> call, @NonNull Throwable t) {
            }
        });
        return view;
    }

}
