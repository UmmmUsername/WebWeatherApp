package com.java.weatherapp.cron;

import com.java.weatherapp.entities.WeatherInfo;
import com.java.weatherapp.repo.MainRepository;
import com.java.weatherapp.repo.WeatherBulk;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class YahooWeatherService {
    private static final String QUERY_URL = "https://query.yahooapis.com/v1/public/yql?&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q=";
    private static final String QUERY_STRING = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")";

    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a z", Locale.US);
    private static final String[] cities = {
            "Moscow,Russia",
            "New York,USA",
            "London,UK"
    };

    @Autowired
    private MainRepository repository;

    @Scheduled(cron = "0 0/30 * * * ?")
    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    public void update() {
        JSONParser parser = new JSONParser();
        List<WeatherBulk> weathers = new ArrayList<>();

        for (String cityToQuery : cities) {
            try {
                String queryString = String.format(QUERY_STRING, cityToQuery);
                String queryUrl = QUERY_URL + URLEncoder.encode(queryString, "UTF8");
                URL url = new URL(queryUrl);
                JSONObject object;

                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    object = (JSONObject) parser.parse(reader);
                }

                JSONObject query = (JSONObject) object.get("query");
                JSONObject results = (JSONObject) query.get("results");
                JSONObject channel = (JSONObject) results.get("channel");

                JSONObject item = (JSONObject) channel.get("item");
                JSONObject condition = (JSONObject) item.get("condition");
                JSONObject wind = (JSONObject) channel.get("wind");
                JSONObject atmosphere = (JSONObject) channel.get("atmosphere");
                JSONObject location = (JSONObject) channel.get("location");

                double temp = parseDouble(condition.get("temp"));
                String text = condition.get("text").toString();
                double windChill = parseDouble(wind.get("chill"));
                double windSpeed = parseDouble(wind.get("speed"));
                double windDirection = parseDouble(wind.get("direction"));
                Date time = parseDate(condition.get("date"));
                double humidity = parseDouble(atmosphere.get("humidity"));
                double pressure = parseDouble(atmosphere.get("pressure"));

                String city = location.get("city").toString();
                String country = location.get("country").toString();

                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setHumidity(humidity);
                weatherInfo.setPressure(pressure);
                weatherInfo.setTemperature(fahrenheitToCelsius(temp));
                weatherInfo.setText(text);
                weatherInfo.setTime(time);
                weatherInfo.setWindChill(fahrenheitToCelsius(windChill));
                weatherInfo.setWindDirection(windDirection);
                weatherInfo.setWindSpeed(mphToMs(windSpeed));

                weathers.add(new WeatherBulk(country, city, weatherInfo));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            try {
                repository.addWeatherInfo(weathers);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static double parseDouble(Object object) {
        return Double.parseDouble(object.toString());
    }

    private static Date parseDate(Object object) throws ParseException {
        return dateFormat.parse(object.toString());
    }

    private double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * (5.0 / 9);
    }

    private double mphToMs(double mph) {
        return mph * 0.44704;
    }
}
