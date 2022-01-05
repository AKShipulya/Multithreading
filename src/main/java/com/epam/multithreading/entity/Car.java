package com.epam.multithreading.entity;

import com.epam.multithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Car {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int id;

    public Car() {
        id = IdGenerator.getCarId();
    }

    public void processRide(Customer customer) {
        LOGGER.info("Car {} starts ride for customer {}", id, customer.getId());
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException exception) {
            LOGGER.info("Error while processing ride ", exception);
            Thread.currentThread().interrupt();
        }
        TaxiDispatch dispatch = TaxiDispatch.getInstance();
        switch (customer.getTaskType()) {
            case LOAD:
                dispatch.createOrder();
            case UNLOAD:
                dispatch.deleteOrder();
            LOGGER.info("Car {} ends ride for customer {}", id, customer.getId());
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Car{");
        stringBuilder.append("id=").append(id);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}