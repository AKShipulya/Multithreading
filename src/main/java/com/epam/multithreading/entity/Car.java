package com.epam.multithreading.entity;

import com.epam.multithreading.exception.TaxiDispatchException;
import com.epam.multithreading.util.CarIdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Car {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int id;

    public Car() {
        id = CarIdGenerator.getCarId();
    }

    public void processRide(Customer customer) throws TaxiDispatchException {
        LOGGER.info("Car {} starts ride for customer {}", id, customer.getId());
        try {
            TimeUnit.SECONDS.sleep(1); //ride progress...
            LOGGER.info("Car {} makes a ride for customer {}...", id, customer.getId());
        } catch (InterruptedException exception) {
            throw new TaxiDispatchException("Error while processing ride ", exception);
        }
        TaxiDispatch dispatch = TaxiDispatch.getInstance();
        dispatch.createOrder();
        dispatch.deleteOrder();
        LOGGER.info("Car {} ends ride for customer {}", id, customer.getId());
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