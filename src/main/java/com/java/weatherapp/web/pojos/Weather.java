package com.java.weatherapp.web.pojos;

import com.java.weatherapp.entities.WeatherInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Weather {
    private double temperature;
    private String text;
    private double windDirection;
    private double windChill;
    private double windSpeed;
    private double humidity;
    private double pressure;
    private String time;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm z");

    public Weather(WeatherInfo weatherInfo) {
        temperature = weatherInfo.getTemperature();
        text = weatherInfo.getText();
        windDirection = weatherInfo.getWindDirection();
        windChill = weatherInfo.getWindChill();
        windSpeed = weatherInfo.getWindSpeed();
        humidity = weatherInfo.getHumidity();
        pressure = weatherInfo.getPressure();
        time = DATE_FORMAT.format(weatherInfo.getTime());
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public double getWindChill() {
        return windChill;
    }

    public void setWindChill(double windChill) {
        this.windChill = windChill;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
