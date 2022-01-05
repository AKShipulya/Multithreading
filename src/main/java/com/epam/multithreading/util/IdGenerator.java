package com.epam.multithreading.util;

public class IdGenerator {
    private static int customerId;
    private static int carId;
    private static int positionNumber;

    private IdGenerator() {
    }

    public static int getCustomerId() {
        return ++customerId;
    }

    public static int getCarId() {
        return ++carId;
    }

    public static int getPositionNumber() {
        return ++positionNumber;
    }
}
