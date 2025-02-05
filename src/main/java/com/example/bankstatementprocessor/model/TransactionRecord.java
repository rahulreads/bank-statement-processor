package com.example.bankstatementprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    private String transactionRef;
    private String accountNumber;
    private String description;
    private double startBalance;
    private double mutation;
    private double endBalance;
}

