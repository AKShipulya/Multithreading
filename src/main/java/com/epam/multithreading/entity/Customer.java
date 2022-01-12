package com.epam.multithreading.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Customer implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    @JsonProperty
    private String name;
    @JsonProperty
    private int id;

    public enum TaskState {
        BEFORE_START, IN_PROGRESS, COMPLETE
    }

       /*
    Default constructor is required for JSON file parsing
     */
    public Customer() {
    }

    @Override
    public void run() {
        LOGGER.info("Customer {} placed order for a ride", id);
        TaxiDispatch dispatch = TaxiDispatch.getInstance();
        Car car = dispatch.obtainAvailableCar();
        LOGGER.info("Customer #{} get car #{}", id, car.getId());
        car.processRide(this);
        dispatch.releaseCar(car);
        LOGGER.info("Ride {} is completed", id);
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Customer customer = (Customer) o;

        if (id != customer.id) {
            return false;
        }
        return name != null ? name.equals(customer.name) : customer.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("name='").append(name).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
