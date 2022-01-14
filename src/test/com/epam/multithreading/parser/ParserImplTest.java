package com.epam.multithreading.parser;

import com.epam.multithreading.entity.Customer;
import com.epam.multithreading.exception.ParserException;
import com.epam.multithreading.parser.impl.ParserImpl;
import com.epam.multithreading.store.CustomersStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class ParserImplTest {

    @Test
    public void testParserShouldReturnListOfParsedValuesFromJson() throws ParserException {
        //given
        String JsonFilePath = "src/test/resources/testCustomers.json";
        CustomersStore store;
        Parser parser = new ParserImpl();
        Customer testCustomer1 = new Customer("Eddard Stark", 1);
        Customer testCustomer2 = new Customer("Robert Baratheon", 2);
        List<Customer> expected = Arrays.asList(testCustomer1, testCustomer2);
        //when
        store = parser.parse(JsonFilePath);
        List<Customer> actual = store.getCustomers();
        //then
        Assert.assertEquals(expected, actual);
    }
}
