package com.epam.multithreading.parser;

import com.epam.multithreading.exception.ParserException;
import com.epam.multithreading.store.CustomersStore;

public interface Parser {
    CustomersStore parse(String filePath) throws ParserException;
}
