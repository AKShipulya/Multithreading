package com.epam.multithreading.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaxiDispatch {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String FILE_PATH = "files/dispatch_hub.txt";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean created = new AtomicBoolean(false);
    private static TaxiDispatch instance;
    private final ReentrantLock orderLock = new ReentrantLock(true);
    private final ReentrantLock carLock = new ReentrantLock(true);
    private final Condition carCondition = carLock.newCondition();
    private final Condition placeOrder = orderLock.newCondition();
    private final Condition deleteOrder = orderLock.newCondition();
    private final Deque<Car> availableCars;
    private final Deque<Car> unavailableCars;
    private final int ordersCapacity;
    private final int carNumber;
    private int currentOrderNumber;

    private TaxiDispatch() {
        InputStream propertyFileStream = getClass().getClassLoader().getResourceAsStream(FILE_PATH);
        Properties properties = new Properties();
        try {
            properties.load(propertyFileStream);
        } catch (IOException exception) {
            LOGGER.warn("Input stream is invalid");
        }
        String capacity = properties.getProperty(TaxiDispatchParameters.CAPACITY.toString());
        String carNum = properties.getProperty(TaxiDispatchParameters.CAR_NUM.toString());
        String orderNumber = properties.getProperty(TaxiDispatchParameters.ORDERS.toString());
        ordersCapacity = Integer.parseInt(capacity);
        this.carNumber = Integer.parseInt(carNum);
        currentOrderNumber = Integer.parseInt(orderNumber);
        availableCars = new ArrayDeque<>();
        unavailableCars = new ArrayDeque<>();
        for (int i = 0; i < this.carNumber; i++) {
            availableCars.addLast(new Car());
        }
    }

    public static TaxiDispatch getInstance() {
        if (!created.get()) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new TaxiDispatch();
                    created.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public int getCarNumber() {
        return carNumber;
    }

    public int getOrdersCapacity() {
        return ordersCapacity;
    }

    public int getCurrentOrderNumber() {
        return currentOrderNumber;
    }

    public Car obtainAvailableCar() {
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
