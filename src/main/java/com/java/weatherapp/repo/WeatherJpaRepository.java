package com.java.weatherapp.repo;

import com.java.weatherapp.entities.City;
import com.java.weatherapp.entities.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface WeatherJpaRepository extends JpaRepository<WeatherInfo, Long> {
    WeatherInfo findByCityAndTime(City city, Date time);

    List<WeatherInfo> findAllByCity(City city);
    List<WeatherInfo> findAllByCityCountryNameAndCityName(String country, String city);
}
