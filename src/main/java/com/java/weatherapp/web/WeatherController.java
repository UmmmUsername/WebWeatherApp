package com.java.weatherapp.web;

import com.java.weatherapp.entities.City;
import com.java.weatherapp.entities.Country;
import com.java.weatherapp.repo.MainRepository;
import com.java.weatherapp.repo.WeatherBulk;
import com.java.weatherapp.web.pojos.Weather;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.spring.JtwigViewResolver;
import org.jtwig.web.servlet.JtwigRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WeatherController {
    @Autowired
    private MainRepository repository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("countries", repository.getAll());

        return "index";
    }

    @GetMapping("/countries")
    @ResponseBody
    public List<String> countries() {
        return repository.getAllCountries(false).stream().map(Country::getName).collect(Collectors.toList());
    }

    @GetMapping("/cities")
    @ResponseBody
    public String[] cities(@RequestParam(value = "country") String country) {
        return repository.getAllCities(country).stream().map(City::getName).toArray(String[]::new);
    }

    @GetMapping("/weather")
    @ResponseBody
    public Weather[] weather(@RequestParam(value = "country") String country, @RequestParam(value = "city") String city) {
        return repository.getWeather(country, city).stream().map(Weather::new).toArray(Weather[]::new);
    }

    @GetMapping("/jdbc")
    public ModelAndView jdbc() {
        List<WeatherBulk> weathers = repository.getAllByJdbc();

        weathers.sort(Comparator.comparing(WeatherBulk::getCountry).thenComparing(WeatherBulk::getCity));

        Map<String, Object> map = new HashMap<>();
        map.put("weathers", weathers);

        return new ModelAndView("jdbc", map);
    }

    @Bean
    public ViewResolver viewResolver() {
        EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
                .configuration()
                .parser()
                .syntax()
                .withStartCode("{%").withEndCode("%}")
                .withStartOutput("{=").withEndOutput("=}")
                .withStartComment("{#").withEndComment("#}")
                .and()
                .and()
                .resources()
                .withDefaultInputCharset(StandardCharsets.UTF_8)
                .and()
                .render()
                .withOutputCharset(StandardCharsets.UTF_8)
                .and()
                .build();

        JtwigRenderer jtwigRenderer = new JtwigRenderer(configuration);

        JtwigViewResolver viewResolver = new JtwigViewResolver();
        viewResolver.setRenderer(jtwigRenderer);
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".twig.html");
        viewResolver.setContentType("text/html; charset=UTF-8");

        return viewResolver;
    }
}
