package com.ochodek.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class OpenWeatherMapException extends RuntimeException {

    @Getter
    private final HttpStatus errorCode;

    public OpenWeatherMapException(String cityName, HttpStatus errorCode) {
        super(cityName);
        this.errorCode = errorCode;
    }


}
