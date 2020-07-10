package com.ochodek.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CityNotFoundAdvice {

    Logger log = LoggerFactory.getLogger(CityNotFoundAdvice.class);

    @ResponseBody
    @ExceptionHandler(OpenWeatherMapException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String cityNotFoundHandler(OpenWeatherMapException exception) {
        return decodeStatusCodeToMessage(exception.getMessage(), exception.getErrorCode());
    }

    private String decodeStatusCodeToMessage(String cityName, HttpStatus statusCode) {
        switch (statusCode) {
            case UNAUTHORIZED:
                return "Wrong api key to OpenWeatherMap, please try again later";
            case NOT_FOUND:
                return String.format("City %s not found", cityName);
            case TOO_MANY_REQUESTS:
                return "Too many requests to OpenWeatherMap, please try again later";
            default:
                log.error(String.format("Unsupported error code %s, need to FIX it", statusCode.value()));
                return  "Unsupported error code, please try again later";
        }
    }
}
