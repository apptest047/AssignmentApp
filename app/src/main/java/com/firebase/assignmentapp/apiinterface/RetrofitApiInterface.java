package com.firebase.assignmentapp.apiinterface;
import com.firebase.assignmentapp.data.location.LocationData;
import com.firebase.assignmentapp.data.weather.WeatherData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiInterface {
@GET("data/2.5/weather?&lang=en")
    Call<WeatherData> getweatherdata(@Query("lat") double lat, @Query("lon") double lon, @Query("appid")
            String appid, @Query("units") String units
    );
@GET("geo/1.0/direct?&limit=5&appid=4066121af6e250a7d9912845e19db5e2")
    Call<List<LocationData>> getlocationdata(
            @Query("q") String q
);
}
