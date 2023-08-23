package com.iyzico.challenge.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SeatRequest {
    @NotBlank(message = "Seat name cannot be blank")
    private String seatName;
}
