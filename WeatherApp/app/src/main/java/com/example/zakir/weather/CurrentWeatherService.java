package com.example.zakir.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Zakir on 01-Jan-18.
 */

public interface CurrentWeatherService {
    @GET()
    Call<CurrentWeatherResponse> getCurrentWeatherData(@Url String endUrl);
    @GET()
    Call<ForcastWeatherResponse> getAllForcastData(@Url String endUrl);
}
