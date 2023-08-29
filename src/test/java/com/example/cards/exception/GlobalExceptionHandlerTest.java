package com.example.cards.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cards.response.GenericResponse;

import java.sql.SQLSyntaxErrorException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {GlobalExceptionHandler.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testHandleExpiredJwtException() {
        ResponseEntity<GenericResponse> actualHandleExpiredJwtExceptionResult = globalExceptionHandler
                .handleExpiredJwtException(new JwtExpiredException("An error occurred", new Throwable()));
        assertTrue(actualHandleExpiredJwtExceptionResult.hasBody());
        assertTrue(actualHandleExpiredJwtExceptionResult.getHeaders().isEmpty());
        assertEquals(401, actualHandleExpiredJwtExceptionResult.getStatusCodeValue());
        GenericResponse body = actualHandleExpiredJwtExceptionResult.getBody();
        assertEquals(403, body.getStatus().intValue());
        assertFalse(body.isSuccess());
        assertEquals("JWT expired at An error occurred", body.getMessage());
        assertNull(body.getData());
    }

    @Test
    public void testHandleCustomException() {
        GenericResponse actualHandleCustomExceptionResult = globalExceptionHandler
                .handleCustomException(new CustomException("An error occurred"));
        assertNull(actualHandleCustomExceptionResult.getData());
        assertFalse(actualHandleCustomExceptionResult.isSuccess());
        assertEquals(400, actualHandleCustomExceptionResult.getStatus().intValue());
        assertEquals("An error occurred", actualHandleCustomExceptionResult.getMessage());
    }

    @Test
    public void testHandleSqlSyntaxException() {
        GenericResponse actualHandleSqlSyntaxExceptionResult = globalExceptionHandler
                .handleSqlSyntaxException(new SQLSyntaxErrorException("Incorrect SQL syntax"));
        assertNull(actualHandleSqlSyntaxExceptionResult.getData());
        assertFalse(actualHandleSqlSyntaxExceptionResult.isSuccess());
        assertEquals(400, actualHandleSqlSyntaxExceptionResult.getStatus().intValue());
        assertEquals("Incorrect SQL syntax", actualHandleSqlSyntaxExceptionResult.getMessage());
    }


}

