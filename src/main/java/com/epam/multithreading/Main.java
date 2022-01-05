package com.epam.multithreading;

import com.epam.multithreading.entity.Customer;
import com.epam.multithreading.exception.TaxiDispatchException;
import com.epam.multithreading.parser.CustomerFileLinesParser;
import com.epam.multithreading.reader.CustomersFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String FILE_PATH = "files/customers.txt";

    public static void main(String[] args) {
        URL fileUrl = Main.class.getClassLoader().getResource(FILE_PATH);
        File file = new File(fileUrl.getFile());
        String filePath = file.getAbsolutePath();
        CustomersFileReader reader = new CustomersFileReader();
        CustomerFileLinesParser parser = new CustomerFileLinesParser();
        try {
            List<Customer> ships;
            List<String> fileLines = reader.readCustomersFile(filePath);
            ships = parser.receiveCustomers(fileLines);
            ExecutorService service = Executors.newFixedThreadPool(ships.size());
            ships.forEach(service::execute);
            service.shutdown();
        } catch (TaxiDispatchException exception) {
            LOGGER.error(exception);
        }
    }
}
