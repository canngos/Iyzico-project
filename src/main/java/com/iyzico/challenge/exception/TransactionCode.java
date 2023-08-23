package com.iyzico.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransactionCode {
    SUCCESS(100, "Success", HttpStatus.OK),
    FLIGHT_ALREADY_EXISTS(101, "Flight already exists", HttpStatus.BAD_REQUEST),
    DATE_FORMAT_ERROR(102, "Date format error. Format is dd-MM-yyyy HH:mm", HttpStatus.BAD_REQUEST),
    FLIGHT_NOT_FOUND(103, "Flight not found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_EXISTS(104, "Seat already exists in the plane", HttpStatus.BAD_REQUEST),
    SEAT_NOT_FOUND(105, "Seat not found", HttpStatus.NOT_FOUND),
    ALREADY_BOOKED(106, "Seat already booked", HttpStatus.BAD_REQUEST),
    PAYMENT_ERROR(107, "Payment error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int id;
    private final String code;
    private final HttpStatus httpStatus;

    TransactionCode(int id, String code, HttpStatus httpStatus) {
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
