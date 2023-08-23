package com.iyzico.challenge.service;

import com.iyzico.challenge.request.ClientRequest;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.request.SeatRequest;
import com.iyzico.challenge.response.*;

public interface FlightService {

    FlightResponse createFlight(FlightRequest flightRequest);

    DefaultMessageResponse deleteFlight(Long flightId);

    DefaultMessageResponse updateFlight(Long flightId, FlightRequest flightRequest);

    SeatResponse addSeat(Long flightId, SeatRequest seatRequest);

    DefaultMessageResponse deleteSeat(Long flightId, Long seatId);

    DefaultMessageResponse updateSeat(Long flightId, Long seatId, SeatRequest seatRequest);

    DetailFlightResponse getAllFlights();

    DefaultMessageResponse bookSeat(Long flightId, Long seatId);

    DefaultMessageResponse bookSeatWithIyzico(Long flightId, Long seatId, ClientRequest clientRequest);
}
