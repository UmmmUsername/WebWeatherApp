package com.java.weatherapp.repo;

import com.java.weatherapp.entities.City;
import com.java.weatherapp.entities.Country;
import com.java.weatherapp.entities.WeatherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MainRepository {
    @Autowired
    private CountriesJpaRepository countriesJpaRepository;

    @Autowired
    private CitiesJpaRepository citiesJpaRepository;

    @Autowired
    private WeatherJpaRepository weatherJpaRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<WeatherInfo> addWeatherInfo(List<WeatherBulk> weathers) {
        Map<String, Country> countries = new HashMap<>();
        Map<String, Map<String, City>> cities = new HashMap<>();

        for (Country country : countriesJpaRepository.findAllWithCities()) {
            countries.put(country.getName(), country);
            Map<String, City> countryCities = new HashMap<>();

            for (City city : country.getCities()) {
                countryCities.put(city.getName(), city);
            }

            cities.put(country.getName(), countryCities);
        }

        List<WeatherInfo> result = new ArrayList<>();

        for (WeatherBulk weatherBulk : weathers) {
            Country country = countries.get(weatherBulk.getCountry());

            if (country == null) {
                country = ensureCountry(weatherBulk.getCountry());
                countries.put(weatherBulk.getCountry(), country);
                cities.put(weatherBulk.getCountry(), new HashMap<>());
            }

            City city = cities.get(weatherBulk.getCountry()).get(weatherBulk.getCity());

            if (city == null) {
                city = ensureCity(country, weatherBulk.getCity());
                cities.get(weatherBulk.getCountry()).put(weatherBulk.getCity(), city);
            }

            WeatherInfo weather = weatherBulk.getWeatherInfo();

            if (weatherJpaRepository.findByCityAndTime(city, weather.getTime()) == null) {
                weather.setCity(city);
                result.add(weather);
            }

            weatherJpaRepository.saveAll(result);
        }

        return result;
    }

    private boolean addWeatherInfoPrivate(String countryName, String cityName, WeatherInfo weather) {
        Country country = ensureCountry(countryName);
        City city = ensureCity(country, cityName);

        weather.setCity(city);

        if (weatherJpaRepository.findByCityAndTime(weather.getCity(), weather.getTime()) != null)
            return false;

        weatherJpaRepository.save(weather);

        return true;
    }

    private Country ensureCountry(String name) {
        Country country = countriesJpaRepository.findByName(name);

        if (country == null) {
            country = new Country();
            country.setName(name);
            countriesJpaRepository.save(country);
        }

        return country;
    }

    private City ensureCity(Country country, String name) {
        City city = citiesJpaRepository.findByCountryAndName(country, name);

        if (city == null) {
            city = new City();
            city.setCountry(country);
            city.setName(name);
            citiesJpaRepository.save(city);
        }

        return city;
    }

    @Transactional(readOnly = true)
    public List<Country> getAll() {
        return countriesJpaRepository.findAllWithCities();
    }

    @Transactional(readOnly = true)
    public List<Country> getAllCountries(boolean forbidLaziness) {
        return countriesJpaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<City> getAllCities(String country) {
        return citiesJpaRepository.findAllByCountryName(country);
    }

    @Transactional(readOnly = true)
    public List<WeatherInfo> getWeather(String country, String city) {
        return weatherJpaRepository.findAllByCityCountryNameAndCityName(country, city);
    }

    @Autowired
    private DataSource jdbcDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Transactional(readOnly = true)
    public List<WeatherBulk> getAllByJdbc() {
        return jdbcTemplate.query("SELECT country.NAME country, city.NAME city, weather.ID id, weather.HUMIDITY humidity, " +
                        "weather.PRESSURE pressure, weather.TEMPERATURE temperature, weather.TEXT text, weather.TIME time, " +
                        "weather.WIND_CHILL windChill, weather.WIND_DIRECTION windDirection, weather.WIND_SPEED windSpeed " +
                        "FROM WEATHER_INFO weather JOIN CITY city ON city.ID = weather.CITY_ID " +
                        "JOIN COUNTRY country ON country.ID = city.COUNTRY_ID ",
                (rs, i) -> Helper.convert(rs)
        );
    }
}
