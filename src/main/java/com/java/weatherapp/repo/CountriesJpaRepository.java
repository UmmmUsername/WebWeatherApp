package com.java.weatherapp.repo;

import com.java.weatherapp.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountriesJpaRepository extends JpaRepository<Country, Long> {
    Country findByName(String name);

    @Query("SELECT DISTINCT country FROM Country country JOIN FETCH country.cities")
    List<Country> findAllWithCities();
}
