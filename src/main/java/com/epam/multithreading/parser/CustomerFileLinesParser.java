package com.epam.multithreading.parser;

import com.epam.multithreading.entity.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerFileLinesParser {
    private static final String DELIMITER = "\\s+";

    public List<Customer> receiveCustomers(List<String> fileLines) {
        List<Customer> customers = new ArrayList<>();
        fileLines.forEach(string -> {
            String taskTypeValue = string.split(DELIMITER)[1];
            Customer.TaskType taskType = Customer.TaskType.valueOf(taskTypeValue);
            customers.add(new Customer(taskType));
        });
        return customers;
    }
}
