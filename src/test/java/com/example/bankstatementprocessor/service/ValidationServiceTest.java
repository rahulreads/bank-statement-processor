package com.example.bankstatementprocessor.service;

import com.example.bankstatementprocessor.model.TransactionRecord;
import com.example.bankstatementprocessor.model.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationServiceTest {

    private final ValidationService validationService = new ValidationService();

    @Test
    void testValidateRecords_DuplicateReference() {
        List<TransactionRecord> records = List.of(
                new TransactionRecord("1001", "NL91RABO0315273637", "Desc", 100.0, -20.0, 80.0),
                new TransactionRecord("1001", "NL91RABO0315273637", "Duplicate", 200.0, -50.0, 150.0)
        );

        List<ValidationError> errors = validationService.validateRecords(records);
        assertEquals(1, errors.size());
        assertEquals("1001", errors.get(0).getTransactionRef());
        assertEquals("Duplicate reference", errors.get(0).getErrorMessage());
    }

    @Test
    void testValidateRecords_EndBalanceMismatch() {
        List<TransactionRecord> records = List.of(
                new TransactionRecord("1002", "NL91RABO0315273637", "Desc", 100.0, -20.0, 85.0)
        );

        List<ValidationError> errors = validationService.validateRecords(records);
        assertEquals(1, errors.size());
        assertEquals("1002", errors.get(0).getTransactionRef());
        assertEquals("End balance mismatch", errors.get(0).getErrorMessage());
    }

    @Test
    void testValidateRecords_NoErrors() {
        List<TransactionRecord> records = List.of(
                new TransactionRecord("1003", "NL91RABO0315273637", "Valid", 100.0, -20.0, 80.0)
        );

        List<ValidationError> errors = validationService.validateRecords(records);
        assertTrue(errors.isEmpty());
    }
}
