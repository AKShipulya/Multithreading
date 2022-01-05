package com.epam.multithreading.reader;

import com.epam.multithreading.exception.TaxiDispatchException;
import com.epam.multithreading.validator.CustomersFileValidator;
import com.epam.multithreading.validator.impl.CustomersFileValidatorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CustomersFileReader {
    private static final Logger LOGGER = LogManager.getLogger();

    private final CustomersFileValidator customersFileValidator = new CustomersFileValidatorImpl();

    public List<String> readCustomersFile(String filePath) throws TaxiDispatchException {
        if (!customersFileValidator.isFileValid(filePath)) {
            throw new TaxiDispatchException("Invalid file path");
        }
        Path path = Paths.get(filePath);
        try {
            List<String> fileLines = Files.readAllLines(path);
            if (!customersFileValidator.areFileLinesValid(fileLines)) {
                throw new TaxiDispatchException("Invalid file lines");
            }
            LOGGER.info("File lines read successfully");
            return fileLines;
        } catch (IOException exception) {
            throw new TaxiDispatchException("Can't read file: " + filePath, exception);
        }
    }
}
