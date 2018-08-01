package com.java.weatherapp.repo;

import com.java.weatherapp.entities.City;
import com.java.weatherapp.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CitiesJpaRepository extends JpaRepository<City, Long> {
    City findByCountryAndName(Country country, String name);

    List<City> findAllByCountry(Country country);

    List<City> findAllByCountryName(String country);

    @Query("SELECT DISTINCT city FROM City city JOIN FETCH city.country country JOIN FETCH city.weather weather")
    List<City> findAllEager();
}
