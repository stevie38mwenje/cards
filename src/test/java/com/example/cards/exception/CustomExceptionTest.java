package com.example.cards.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CustomExceptionTest {
    @Test
    public void testConstructor() {
        CustomException actualCustomException = new CustomException("An error occurred");
        assertNull(actualCustomException.getCause());
        assertEquals(0, actualCustomException.getSuppressed().length);
        assertEquals("An error occurred", actualCustomException.getMessage());
        assertEquals("An error occurred", actualCustomException.getLocalizedMessage());
    }
}

