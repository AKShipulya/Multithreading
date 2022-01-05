package com.epam.multithreading.validator;

import java.util.List;

public interface CustomersFileValidator {
    boolean isFileValid(String filePath);

    boolean areFileLinesValid(List<String> fileLines);
}
