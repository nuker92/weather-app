package com.ochodek.server.exception;

import com.ochodek.server.response.MoreThanOneCityWithNameResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MoreThanOneCityWithNameAdvice {

    @ResponseBody
    @ExceptionHandler(MoreThanOneCityWithNameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public MoreThanOneCityWithNameResponse cityNotFoundHandler(MoreThanOneCityWithNameException exception) {
        return new MoreThanOneCityWithNameResponse(
                "There are more than one city with specific name",
                exception.getCityName(),
                exception.getCountryCodes()
        );
    }

}
