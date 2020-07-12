package com.ochodek.server.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastCacheConfig {

    @Value("${cache.ttl.actualweather}")
    private int actualWeatherCacheTime;

    @Value("${cache.ttl.forecast}")
    private int forecastCacheTime;


    @Bean
    public Config hazelCastConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-cache");
        config.getMapConfigs().put("actualWeather", getMapConfigForActualWeather());
        config.getMapConfigs().put("forecast", getMapConfigForForecast());


        return config;
    }

    private MapConfig getMapConfigForActualWeather() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        mapConfig.setTimeToLiveSeconds(actualWeatherCacheTime);
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(10485760, MaxSizeConfig.MaxSizePolicy.PER_NODE));
        return mapConfig;
    }

    private MapConfig getMapConfigForForecast() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        mapConfig.setTimeToLiveSeconds(forecastCacheTime);
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(52428800, MaxSizeConfig.MaxSizePolicy.PER_NODE));
        return mapConfig;
    }

}
