package com.ochodek.server.util;

import com.ochodek.server.entity.City;
import com.ochodek.server.entity.Weather;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.WeatherDao;
import com.ochodek.server.model.inner.OwmWeather;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface WeatherObjectsParser {

    static List<Weather> parseActualWeatherCityObjectToWeatherList(ActualWeatherAppCity actualWeatherAppCity, City city) {
        return actualWeatherAppCity.getOwmWeather().stream()
                .map(owmWeather -> parseActualWeatherAppCityObjectToWeather(owmWeather, actualWeatherAppCity, city))
                .collect(Collectors.toList());
    }

    static Weather parseActualWeatherAppCityObjectToWeather(OwmWeather owmWeather, ActualWeatherAppCity actualWeatherAppCity, City city) {
        Weather weather = new Weather();
        weather.setCity(city);
        weather.setWeatherTime(LocalDateTime.now());
        weather.setMainInfo(owmWeather.getMain());
        weather.setDescription(owmWeather.getDescription());
        weather.setVisibility(actualWeatherAppCity.getVisibility());
        if (actualWeatherAppCity.getWind() != null) {
            weather.setWindSpeed(actualWeatherAppCity.getWind().getSpeed());
            weather.setWindDirection(actualWeatherAppCity.getWind().getDegree());
        }
        weather.setClouds(actualWeatherAppCity.getClouds() != null ? actualWeatherAppCity.getClouds().getCloudiness() : null);
        weather.setRainOneHour(actualWeatherAppCity.getRain() != null ? actualWeatherAppCity.getRain().getOneHour() : null);
        weather.setSnowOneHour(actualWeatherAppCity.getSnow() != null ? actualWeatherAppCity.getSnow().getOneHour() : null);
        if (actualWeatherAppCity.getMainWeatherInfo() != null) {
            weather.setPressure(actualWeatherAppCity.getMainWeatherInfo().getPressure());
            weather.setHumidity(actualWeatherAppCity.getMainWeatherInfo().getHumidity());
            weather.setTemperature(actualWeatherAppCity.getMainWeatherInfo().getTemperature());
            weather.setFeelsLikeTemperature(actualWeatherAppCity.getMainWeatherInfo().getFeelsLikeTemperature());
        }

        return weather;
    }

    static WeatherDao parseWeatherToWeatherDao(Weather weather) {
        WeatherDao weatherDao = new WeatherDao();
        weatherDao.setWeatherTime(weather.getWeatherTime());
        weatherDao.setMainInfo(weather.getMainInfo());
        weatherDao.setDescription(weather.getDescription());
        weatherDao.setVisibility(weather.getVisibility());
        weatherDao.setWindSpeed(weather.getWindSpeed());
        weatherDao.setWindDirection(weather.getWindDirection());
        weatherDao.setClouds(weather.getClouds());
        weatherDao.setRainOneHour(weather.getRainOneHour());
        weatherDao.setSnowOneHour(weather.getSnowOneHour());
        weatherDao.setPressure(weather.getPressure());
        weatherDao.setHumidity(weather.getHumidity());
        weatherDao.setTemperature(weather.getTemperature());
        weatherDao.setFeelsLikeTemperature(weather.getFeelsLikeTemperature());
        return weatherDao;
    }

}
