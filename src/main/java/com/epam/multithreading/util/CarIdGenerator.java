package com.epam.multithreading.util;

public class CarIdGenerator {
    private static int carId;

    private CarIdGenerator() {
    }

    public static int getCarId() {
        return ++carId;
    }

}
