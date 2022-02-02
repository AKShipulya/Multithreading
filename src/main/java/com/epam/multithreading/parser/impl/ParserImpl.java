package com.epam.multithreading.parser.impl;

import com.epam.multithreading.parser.Parser;
import com.epam.multithreading.store.CustomersStore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ParserImpl implements Parser {
    private static final Logger LOGGER = LogManager.getLogger();

    public CustomersStore parse(String filePath) {
        CustomersStore customers = new CustomersStore();
        ObjectMapper mapper = new ObjectMapper();
        try (JsonParser jsonParser = mapper.createParser(new File(filePath))) {
            customers = jsonParser.readValueAs(CustomersStore.class);
        } catch (IOException exception) {
            LOGGER.warn(String.format("Error during the file parsing: %s", filePath), exception);
        }
        return customers;
    }
}
