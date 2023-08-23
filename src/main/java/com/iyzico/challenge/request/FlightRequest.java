package com.iyzico.challenge.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
@Getter
@Setter
public class FlightRequest {
    @NotBlank(message = "flightName field cannot be empty")
    private String flightName;
    @NotBlank(message = "origin field cannot be empty")
    private String origin;
    @NotBlank(message = "destination field cannot be empty")
    private String destination;
    @NotBlank(message = "departureTime field cannot be empty")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-((19|20)\\d\\d) ([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "departureTime field must be in the format dd-MM-yyyy HH:mm")
    private String departureTime;
    @NotBlank(message = "arrivalTime field cannot be empty")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-((19|20)\\d\\d) ([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "departureTime field must be in the format dd-MM-yyyy HH:mm")
    private String arrivalTime;
    @NotNull(message = "price field cannot be empty")
    @Digits(integer = 10, fraction = 2, message = "price field cannot be more than 9999999999,99")
    private BigDecimal price;
}
