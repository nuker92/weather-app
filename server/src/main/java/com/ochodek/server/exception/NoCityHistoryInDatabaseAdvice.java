package com.ochodek.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoCityHistoryInDatabaseAdvice {

    @ResponseBody
    @ExceptionHandler(NoCityHistoryInDatabaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public String cityNotFoundHandler(NoCityHistoryInDatabaseException exception) {
        return String.format("There is no history for city %s in our database", exception.getMessage());
    }

}
