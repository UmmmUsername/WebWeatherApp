package com.java.weatherapp.repo;

import com.java.weatherapp.entities.WeatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

class Helper {
    static WeatherBulk convert(ResultSet resultSet) throws SQLException {
        WeatherInfo info = new WeatherInfo();
        info.setId(resultSet.getLong("id"));
        info.setHumidity(resultSet.getDouble("humidity"));
        info.setPressure(resultSet.getDouble("pressure"));
        info.setTemperature(resultSet.getDouble("temperature"));
        info.setText(resultSet.getString("text"));
        info.setTime(resultSet.getDate("time"));
        info.setWindChill(resultSet.getDouble("windChill"));
        info.setWindDirection(resultSet.getDouble("windDirection"));
        info.setWindSpeed(resultSet.getDouble("windSpeed"));

        WeatherBulk bulk = new WeatherBulk();
        bulk.setCity(resultSet.getString("city"));
        bulk.setCountry(resultSet.getString("country"));
        bulk.setWeatherInfo(info);

        return bulk;
    }
}
