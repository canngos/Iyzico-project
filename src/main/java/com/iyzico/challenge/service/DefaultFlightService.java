package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.FlightDto;
import com.iyzico.challenge.dto.SeatDto;
import com.iyzico.challenge.entity.BookedSeat;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.Status;
import com.iyzico.challenge.exception.TransactionCode;
import com.iyzico.challenge.repository.BookedSeatRepository;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.request.SeatRequest;
import com.iyzico.challenge.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultFlightService implements FlightService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final PaymentServiceClients paymentServiceClients;
    private final BookedSeatRepository bookedSeatRepository;

    @Override
    public FlightResponse createFlight(FlightRequest flightRequest) {
        flightRepository.findByFlightName(flightRequest.getFlightName()).ifPresent(flight -> {
            throw new BusinessException(TransactionCode.FLIGHT_ALREADY_EXISTS);
        });

        Flight flight = new Flight();
        mapFlight(flightRequest, flight);

        FlightResponse flightResponse = new FlightResponse();
        FlightResponseBody body = new FlightResponseBody();
        body.setFlightId(flight.getFlightId());
        body.setMessage("Flight created successfully");
        flightResponse.setBody(new BaseBody<>(body));
        flightResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Flight " + flight.getFlightName() + " with id " + flight.getFlightId() + " created successfully");
        return flightResponse;
    }

    @Override
    public DefaultMessageResponse deleteFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));
        flightRepository.delete(flight);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Flight deleted successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Flight " + flight.getFlightName() + " with id " + flight.getFlightId() + " deleted successfully");
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse updateFlight(Long flightId, FlightRequest flightRequest) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));

        mapFlight(flightRequest, flight);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Flight " + flightId + " updated successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Flight " + flight.getFlightName() + " with id " + flight.getFlightId() + " updated successfully");
        return defaultMessageResponse;
    }

    @Override
    public SeatResponse addSeat(Long flightId, SeatRequest seatRequest) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));
        Optional<Seat> seatCheck = seatRepository.findBySeatNameAndFlight(seatRequest.getSeatName(), flight);
        if (seatCheck.isPresent()) {
            log.error("Seat " + seatRequest.getSeatName() + " already exists in flight " + flight.getFlightId());
            throw new BusinessException(TransactionCode.SEAT_ALREADY_EXISTS);
        }

        Seat seat = new Seat();
        seat.setSeatName(seatRequest.getSeatName());
        seat.setFlight(flight);

        seatRepository.save(seat);

        SeatResponse seatResponse = new SeatResponse();
        SeatResponseBody body = new SeatResponseBody();
        body.setSeatId(seat.getSeatId());
        body.setMessage("Seat added to flight " + flight.getFlightId() + " successfully");
        seatResponse.setBody(new BaseBody<>(body));
        seatResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Seat " + seat.getSeatName() + " with id " + seat.getSeatId() + " added to flight " + flight.getFlightId() + " successfully");
        return seatResponse;
    }

    @Override
    public DefaultMessageResponse deleteSeat(Long flightId, Long seatId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));
        Seat seat = seatRepository.findBySeatIdAndFlight(seatId, flight).orElseThrow(() -> new BusinessException(TransactionCode.SEAT_NOT_FOUND));

        seatRepository.delete(seat);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Seat " + seatId + " deleted successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Seat " + seat.getSeatName() + " deleted from flight " + flight.getFlightId() + " successfully");
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse updateSeat(Long flightId, Long seatId, SeatRequest seatRequest) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));
        Seat seat = seatRepository.findBySeatIdAndFlight(seatId, flight).orElseThrow(() -> new BusinessException(TransactionCode.SEAT_NOT_FOUND));

        seat.setSeatName(seatRequest.getSeatName());
        seatRepository.save(seat);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Seat " + seatId + " updated for flight " + flightId + " successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
        log.info("Seat " + seat.getSeatId() + " updated to " + seat.getSeatName() + " for flight " + flight.getFlightId() + " successfully");
        return defaultMessageResponse;
    }

    @Override
    public DetailFlightResponse getAllFlights() {
        List<Flight> flights = flightRepository.findAll();

        DetailFlightResponse detailFlightResponse = new DetailFlightResponse();
        DetailFlightResponseBody body = new DetailFlightResponseBody();
        List<FlightDto> flightDtoList = new ArrayList<>();
        for (Flight flight : flights) {
            FlightDto flightDto = new FlightDto();
            BeanUtils.copyProperties(flight, flightDto);
            List<Seat> seats = seatRepository.findAllByFlightAndIsReservedFalse(flight);
            List<SeatDto> seatDtoList = new ArrayList<>();
            for (Seat seat : seats) {
                SeatDto seatDto = new SeatDto();
                BeanUtils.copyProperties(seat, seatDto);
                seatDtoList.add(seatDto);
            }
            flightDto.setAvaliableSeats(seatDtoList);
            flightDtoList.add(flightDto);
        }
        body.setFlightList(flightDtoList);
        detailFlightResponse.setBody(new BaseBody<>(body));
        detailFlightResponse.setStatus(new Status(TransactionCode.SUCCESS));
        return detailFlightResponse;
    }

    @Override
    public DefaultMessageResponse bookSeat(Long flightId, Long seatId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new BusinessException(TransactionCode.FLIGHT_NOT_FOUND));
        Seat seat = seatRepository.findBySeatIdAndFlight(seatId, flight).orElseThrow(() -> new BusinessException(TransactionCode.SEAT_NOT_FOUND));
        try {
            if (Boolean.TRUE.equals(seat.getIsReserved())) {
                log.error("Seat " + seatId + " already booked for flight " + flightId);
                throw new BusinessException(TransactionCode.ALREADY_BOOKED);
            }
            makePayment(flight.getPrice());
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setSeat(seat);
            bookedSeat.setFlight(flight);
            bookedSeatRepository.save(bookedSeat);
            seat.setIsReserved(true);
            seatRepository.save(seat);

            DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
            DefaultMessageBody body = new DefaultMessageBody("Seat " + seatId + " booked successfully");
            defaultMessageResponse.setBody(new BaseBody<>(body));
            defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
            log.info("Seat " + seat.getSeatName() + " booked successfully for flight id " + flight.getFlightId());
            return defaultMessageResponse;
        }
        catch(DataIntegrityViolationException e){
            log.error("Seat " + seatId + " already booked for flight " + flightId);
            throw new BusinessException(TransactionCode.ALREADY_BOOKED);
        }

    }

    private void mapFlight(FlightRequest flightRequest, Flight flight) {
        BeanUtils.copyProperties(flightRequest, flight);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            flight.setArrivalTime(LocalDateTime.parse(flightRequest.getArrivalTime(), formatter));
            flight.setDepartureTime(LocalDateTime.parse(flightRequest.getDepartureTime(), formatter));
        }catch (Exception e){
            log.error("Date format error: " + e.getMessage());
            throw new BusinessException(TransactionCode.DATE_FORMAT_ERROR);
        }
        flightRepository.save(flight);
    }

    private void makePayment(BigDecimal price) {
        CompletableFuture<String> future =  paymentServiceClients.call(price);
        try {
            future.get();
        } catch (Exception e) {
            log.error("Payment error: " + e.getMessage());
            throw new BusinessException(TransactionCode.PAYMENT_ERROR);
        }
    }
}
