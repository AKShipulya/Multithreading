package com.epam.multithreading.parser.impl;

import com.epam.multithreading.exception.ParserException;
import com.epam.multithreading.parser.Parser;
import com.epam.multithreading.store.CustomersStore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ParserImpl implements Parser {

    public CustomersStore parse(String filePath) throws ParserException {
        ObjectMapper mapper = new ObjectMapper();
        CustomersStore passengers;
        try (JsonParser jsonParser = mapper.createParser(new File(filePath))) {
            passengers = jsonParser.readValueAs(CustomersStore.class);
        } catch (IOException exception) {
            throw new ParserException("Error during file parsing: " + filePath, exception);
        }
        return passengers;
    }
}
