package com.ochodek.server.schedule;

import com.ochodek.server.service.OpenWeatherMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherImportScheduler {

    private final Logger log = LoggerFactory.getLogger(WeatherImportScheduler.class);

    private final OpenWeatherMapService openWeatherMapService;

    @Autowired
    public WeatherImportScheduler(OpenWeatherMapService openWeatherMapService) {
        this.openWeatherMapService = openWeatherMapService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void collectWeatherForAllCitiesInDatabase() {
        log.info("********** START SCHEDULER FOR COLLECTING WEATHER FOR ALL CITIES IN DATABASE **********");
        openWeatherMapService.updateWeatherForAllCitiesInDatabase();
        log.info("********** END SCHEDULER FOR COLLECTING WEATHER FOR ALL CITIES IN DATABASE **********");
    }

}
