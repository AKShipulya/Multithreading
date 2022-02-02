package com.epam.multithreading.store;

import com.epam.multithreading.entity.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomersStore {

    @JsonProperty
    private List<Customer> customers;

    /**
     * Default constructor required for JSON file parsing
     */
    public CustomersStore() {
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("CustomersStore{");
        stringBuilder.append("customers=").append(customers);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
