package com.ochodek.server.exception;

public class NoCityHistoryInDatabaseException extends RuntimeException {

    public NoCityHistoryInDatabaseException(String cityName) {
        super(cityName);
    }
}
