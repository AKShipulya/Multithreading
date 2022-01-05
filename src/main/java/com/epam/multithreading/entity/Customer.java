package com.epam.multithreading.entity;

import com.epam.multithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Customer implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int id;
    private final TaskType taskType;
    private TaskState taskState;

    public enum TaskType {
        LOAD, UNLOAD
    }

    public enum TaskState {
        BEFORE_START, IN_PROGRESS, COMPLETE
    }

    public Customer(TaskType taskType) {
        this.id = IdGenerator.getCustomerId();
        this.taskType = taskType;
        this.taskState = TaskState.BEFORE_START;
    }

    @Override
    public void run() {
        this.taskState = TaskState.IN_PROGRESS;
        LOGGER.info("Ride {} runs", id);
        TaxiDispatch dispatch = TaxiDispatch.getInstance();
        Car car = dispatch.obtainAvailableCar();
        LOGGER.info("Customer #{} get car #{}", id, car.getId());
        car.processRide(this);
        dispatch.releaseCar(car);
        this.taskState = TaskState.COMPLETE;
        LOGGER.info("Ride {} is completed", id);
    }

    public int getId() {
        return id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
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
        if (taskType != customer.taskType) {
            return false;
        }
        return taskState == customer.taskState;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (taskType != null ? taskType.hashCode() : 0);
        result = 31 * result + (taskState != null ? taskState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Ship{");
        stringBuilder.append("shipId=").append(id);
        stringBuilder.append(", taskType=").append(taskType);
        stringBuilder.append(", taskState=").append(taskState);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
