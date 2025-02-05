package com.example.bankstatementprocessor.service;

import com.example.bankstatementprocessor.model.TransactionRecord;
import com.example.bankstatementprocessor.model.ValidationError;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ValidationService {
    public List<ValidationError> validateRecords(List<TransactionRecord> records) {
        Set<String> seenRefs = new HashSet<>();
        List<ValidationError> errors = new ArrayList<>();

        for (TransactionRecord record : records) {
            if (!seenRefs.add(record.getTransactionRef())) {
                errors.add(new ValidationError(record.getTransactionRef(), "Duplicate reference"));
            }

            double expectedBalance = record.getStartBalance() + record.getMutation();
            if (Math.abs(expectedBalance - record.getEndBalance()) > 0.0001) {
                errors.add(new ValidationError(record.getTransactionRef(), "End balance mismatch"));
            }
        }
        return errors;
    }
}

