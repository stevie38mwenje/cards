package com.example.cards.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.http.HttpStatus;

public class CustomHttpExceptionTest {
    @Test
    public void testConstructor() {
        CustomHttpException actualCustomHttpException = new CustomHttpException(HttpStatus.CONTINUE,
                "an error occurred");

        assertNull(actualCustomHttpException.getCause());
        assertEquals(0, actualCustomHttpException.getSuppressed().length);
        assertEquals(HttpStatus.CONTINUE, actualCustomHttpException.getStatus());
        assertEquals("an error occurred", actualCustomHttpException.getReason());
        assertEquals("an error occurred", actualCustomHttpException.getMessage());
        assertEquals("an error occurred", actualCustomHttpException.getLocalizedMessage());
    }
}

