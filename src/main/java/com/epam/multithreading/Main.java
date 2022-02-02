package com.epam.multithreading;

import com.epam.multithreading.exception.ParserException;
import com.epam.multithreading.parser.Parser;
import com.epam.multithreading.parser.impl.ParserImpl;
import com.epam.multithreading.store.CustomersStore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String JSON_FILE_PATH = "src/test/resources/customers.json";

    public static void main(String[] args) throws ParserException {
        Parser jsonParser = new ParserImpl();
        CustomersStore customers = jsonParser.parse(JSON_FILE_PATH);

        ExecutorService service = Executors.newFixedThreadPool(customers.getCustomers().size());
        customers.getCustomers().forEach(service::execute);
        service.shutdown();
    }
}
