package com.iyzico.challenge.response;

import com.iyzico.challenge.dto.FlightDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetailFlightResponseBody {
    List<FlightDto> flightList;
}
