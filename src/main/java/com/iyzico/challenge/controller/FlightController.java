package com.iyzico.challenge.controller;

import com.iyzico.challenge.request.ClientRequest;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.request.SeatRequest;
import com.iyzico.challenge.response.DefaultMessageResponse;
import com.iyzico.challenge.response.DetailFlightResponse;
import com.iyzico.challenge.response.FlightResponse;
import com.iyzico.challenge.response.SeatResponse;
import com.iyzico.challenge.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping(value = "/create")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest flightRequest) {
        return new ResponseEntity<>(flightService.createFlight(flightRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{flightId}")
    public ResponseEntity<DefaultMessageResponse> deleteFlight(@Valid @PathVariable Long flightId) {
        return new ResponseEntity<>(flightService.deleteFlight(flightId), HttpStatus.OK);
    }

    @PutMapping(value = "/{flightId}")
    public ResponseEntity<DefaultMessageResponse> updateFlight(@Valid @PathVariable Long flightId, @Valid @RequestBody FlightRequest flightRequest) {
        return new ResponseEntity<>(flightService.updateFlight(flightId, flightRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/add/seat/{flightId}")
    public ResponseEntity<SeatResponse> addSeat(@Valid @PathVariable Long flightId, @Valid @RequestBody SeatRequest seatRequest) {
        return new ResponseEntity<>(flightService.addSeat(flightId, seatRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{flightId}/seat/{seatId}")
    public ResponseEntity<DefaultMessageResponse> deleteSeat(@Valid @PathVariable Long flightId, @Valid @PathVariable Long seatId) {
        return new ResponseEntity<>(flightService.deleteSeat(flightId, seatId), HttpStatus.OK);
    }

    @PutMapping(value = "/{flightId}/seat/{seatId}")
    public ResponseEntity<DefaultMessageResponse> updateSeat(@Valid @PathVariable Long flightId, @Valid @PathVariable Long seatId, @Valid @RequestBody SeatRequest seatRequest) {
        return new ResponseEntity<>(flightService.updateSeat(flightId, seatId, seatRequest), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<DetailFlightResponse> getAllFlights() {
        return new ResponseEntity<>(flightService.getAllFlights(), HttpStatus.OK);
    }

    @PostMapping(value = "/{flightId}/book/{seatId}")
    public ResponseEntity<DefaultMessageResponse> bookSeat(@Valid @PathVariable Long flightId, @Valid @PathVariable Long seatId) {
        return new ResponseEntity<>(flightService.bookSeat(flightId, seatId), HttpStatus.OK);
    }

    @PostMapping(value = "/{flightId}/book/{seatId}/iyzico")
    public ResponseEntity<DefaultMessageResponse> bookSeatWithIyzico(@Valid @PathVariable Long flightId, @Valid @PathVariable Long seatId, @Valid @RequestBody ClientRequest clientRequest) {
        return new ResponseEntity<>(flightService.bookSeatWithIyzico(flightId, seatId, clientRequest), HttpStatus.OK);
    }
}
