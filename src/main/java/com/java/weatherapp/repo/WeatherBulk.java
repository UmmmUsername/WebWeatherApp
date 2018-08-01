package com.java.weatherapp.repo;

import com.java.weatherapp.entities.WeatherInfo;

public class WeatherBulk {
    private String country;
    private String city;
    private WeatherInfo weatherInfo;

    public WeatherBulk() {}

    public WeatherBulk(String country, String city, WeatherInfo weatherInfo) {
        this.country = country;
        this.city = city;
        this.weatherInfo = weatherInfo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }
}
