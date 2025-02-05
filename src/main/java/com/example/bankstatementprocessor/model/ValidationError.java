package com.example.bankstatementprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    private String transactionRef;
    private String errorMessage;
}

