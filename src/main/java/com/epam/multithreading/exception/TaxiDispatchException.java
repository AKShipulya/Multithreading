package com.epam.multithreading.exception;

public class TaxiDispatchException extends Exception{

    public TaxiDispatchException() {
        super();
    }

    public TaxiDispatchException(String message) {
        super(message);
    }

    public TaxiDispatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaxiDispatchException(Throwable cause) {
        super(cause);
    }
}
