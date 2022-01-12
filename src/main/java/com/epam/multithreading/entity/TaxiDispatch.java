package com.epam.multithreading.entity;

import com.epam.multithreading.exception.TaxiDispatchException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaxiDispatch {
    private static final String RESOURCES_FILE_NAME = "dispatchHub";
    private static final String DISPATCHER_CARS_NUMBER_KEY = "dispatcher.carsNumber";
    private static final String DISPATCHER_POSSIBLE_ORDERS_CAPACITY_KEY = "dispatcher.ordersCapacity";
    private static final String DISPATCHER_ORDERS_KEY = "dispatcher.orders";
    private static final Lock LOCK = new ReentrantLock();
    private static final Lock CAR_LOCK = new ReentrantLock();
    private static final Lock ORDER_LOCK = new ReentrantLock();
    private static final AtomicBoolean CREATED = new AtomicBoolean(false);

    private static TaxiDispatch instance;

    private final Condition carCondition = CAR_LOCK.newCondition();
    private final Condition placeOrder = ORDER_LOCK.newCondition();
    private final Condition deleteOrder = ORDER_LOCK.newCondition();
    private final Deque<Car> availableCars;
    private final Deque<Car> unavailableCars;
    private final int initialCarsNumber;
    private final int possibleOrdersCapacity;
    private final Semaphore semaphore;

    private int currentOrderNumber;

    private TaxiDispatch() {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCES_FILE_NAME);
        possibleOrdersCapacity = Integer.parseInt(bundle.getString(DISPATCHER_POSSIBLE_ORDERS_CAPACITY_KEY));
        initialCarsNumber = Integer.parseInt(bundle.getString(DISPATCHER_CARS_NUMBER_KEY));
        this.currentOrderNumber = Integer.parseInt(bundle.getString(DISPATCHER_ORDERS_KEY));
        semaphore = new Semaphore(possibleOrdersCapacity);
        availableCars = new ArrayDeque<>();
        unavailableCars = new ArrayDeque<>();
        for (int i = 0; i < this.initialCarsNumber; i++) {
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

    public Car obtainAvailableCar() throws TaxiDispatchException {
        try {
            semaphore.acquire();
            CAR_LOCK.lock();
            while (availableCars.isEmpty()) {
                carCondition.await();
            }
            Car car = availableCars.removeLast();
            unavailableCars.addLast(car);
            return car;
        } catch (InterruptedException exception) {
            throw new TaxiDispatchException("Error while car obtaining ", exception);
        } finally {
            CAR_LOCK.unlock();
        }
    }


    public void releaseCar(Car car) {
        try {
            CAR_LOCK.lock();
            unavailableCars.remove(car);
            availableCars.addLast(car);
            carCondition.signal();
            semaphore.release();
        } finally {
            CAR_LOCK.unlock();
        }
    }

    public void createOrder() throws TaxiDispatchException {
        try {
            ORDER_LOCK.lock();
            try {
                while (currentOrderNumber == possibleOrdersCapacity) {
                    placeOrder.await();
                }
            } catch (InterruptedException exception) {
                throw new TaxiDispatchException("Error while orders processing ", exception);
            }
            currentOrderNumber++;
            deleteOrder.signal();
        } finally {
            ORDER_LOCK.unlock();
        }
    }

    public void deleteOrder() throws TaxiDispatchException {
        try {
            ORDER_LOCK.lock();
            try {
                while (currentOrderNumber == 0) {
                    deleteOrder.await();
                }
            } catch (InterruptedException exception) {
                throw new TaxiDispatchException("Error while orders processing ", exception);
            }
            currentOrderNumber--;
            placeOrder.signal();
        } finally {
            ORDER_LOCK.unlock();
        }
    }
}
