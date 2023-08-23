package com.iyzico.challenge.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final TransactionCode transactionCode;

    public BusinessException(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }
}
