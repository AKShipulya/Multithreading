package com.epam.multithreading.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaxiDispatch {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String RESOURCES_FILE_NAME = "dispatchHub";
    private static final String DISPATCHER_CAPACITY_KEY = "dispatcher.capacity";
    private static final String DISPATCHER_CARS_NUMBER_KEY = "dispatcher.carsNumber";
    private static final String DISPATCHER_ORDERS_KEY = "dispatcher.orders";
    private static final Lock LOCK = new ReentrantLock();
    private static final AtomicBoolean CREATED = new AtomicBoolean(false);

    private static TaxiDispatch instance;

    private final Semaphore semaphore = new Semaphore(3);
    private final Lock orderLock = new ReentrantLock();
    private final Lock carLock = new ReentrantLock();
    private final Condition carCondition = carLock.newCondition();
    private final Condition placeOrder = orderLock.newCondition();
    private final Condition deleteOrder = orderLock.newCondition();
    private final Deque<Car> availableCars;
    private final Deque<Car> unavailableCars;

    private int ordersCapacity;
    private int carNumber;
    private int currentOrderNumber;

    private TaxiDispatch() {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCES_FILE_NAME);
        this.ordersCapacity = Integer.parseInt(bundle.getString(DISPATCHER_CAPACITY_KEY));
        this.carNumber = Integer.parseInt(bundle.getString(DISPATCHER_CARS_NUMBER_KEY));
        this.currentOrderNumber = Integer.parseInt(bundle.getString(DISPATCHER_ORDERS_KEY));
        availableCars = new ArrayDeque<>();
        unavailableCars = new ArrayDeque<>();
        for (int i = 0; i < this.carNumber; i++) {
            availableCars.addLast(new Car());
        }
    }

    public static TaxiDispatch getInstance() {
        if (!CREATED.get()) {
            try {
                LOCK.lock();
                if (instance == null) {
                    instance = new TaxiDispatch();
                    CREATED.set(true);
                }
            } finally {
                LOCK.unlock();
            }
        }
        return instance;
    }

    public Car obtainAvailableCar() {
        try {
            semaphore.acquire();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        try {
            carLock.lock();
            try {
                while (availableCars.isEmpty()) {
                    carCondition.await();
                }
            } catch (InterruptedException exception) {
                LOGGER.error("Error while car obtaining ", exception);
                Thread.currentThread().interrupt();
            }
            Car car = availableCars.removeLast();
            unavailableCars.addLast(car);
            return car;
        } finally {
            carLock.unlock();
        }
    }

    public void releaseCar(Car car) {
        try {
            carLock.lock();
            unavailableCars.remove(car);
            availableCars.addLast(car);
            carCondition.signal();
            semaphore.release();
        } finally {
            carLock.unlock();
        }
    }

    public void createOrder() {
        try {
            orderLock.lock();
            try {
                while (currentOrderNumber == ordersCapacity) {
                    placeOrder.await();
                }
            } catch (InterruptedException exception) {
                LOGGER.error("Error while orders processing ", exception);
                Thread.currentThread().interrupt();
            }
            currentOrderNumber++;
            deleteOrder.signal();
        } finally {
            orderLock.unlock();
        }
    }

    public void deleteOrder() {
        try {
            orderLock.lock();
            try {
                while (currentOrderNumber == 0) {
                    deleteOrder.await();
                }
            } catch (InterruptedException exception) {
                LOGGER.error("Error while orders processing ", exception);
                Thread.currentThread().interrupt();
            }
            currentOrderNumber--;
            placeOrder.signal();
        } finally {
            orderLock.unlock();
        }
    }
}
