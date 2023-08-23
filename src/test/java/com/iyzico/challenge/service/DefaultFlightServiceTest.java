package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.BookedSeat;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.repository.BookedSeatRepository;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.request.SeatRequest;
import com.iyzico.challenge.response.DefaultMessageResponse;
import com.iyzico.challenge.response.DetailFlightResponse;
import com.iyzico.challenge.response.FlightResponse;
import com.iyzico.challenge.response.SeatResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultFlightServiceTest {

    @InjectMocks
    private DefaultFlightService defaultFlightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PaymentServiceClients paymentServiceClients;

    @Mock
    private BookedSeatRepository bookedSeatRepository;

    private Long flightId;
    private Long seatId;

    @BeforeEach
    void setUp() {
        flightId = 1L;
        seatId = 1L;
    }

    @Test
    void testCreateFlight_whenFlightAlreadyExist_returnErrorCode101() {
        when(flightRepository.findByFlightName(anyString())).thenReturn(Optional.of(getFlight()));

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.createFlight(getFlightRequest()));
        assertEquals(101, businessException.getTransactionCode().getId());
    }

    @Test
    void testCreateFlight_whenFormatErrorOccur_returnErrorCode102() {
        FlightRequest flightRequest = getFlightRequest();
        flightRequest.setArrivalTime("wrong format");

        when(flightRepository.findByFlightName(anyString())).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.createFlight(flightRequest));
        assertEquals(102, businessException.getTransactionCode().getId());
    }

    @Test
    void testCreateFlight_whenCalledValid_returnSuccess() {
        FlightRequest flightRequest = getFlightRequest();

        when(flightRepository.findByFlightName(anyString())).thenReturn(Optional.empty());

        FlightResponse flightResponse = defaultFlightService.createFlight(flightRequest);
        assertEquals("100", flightResponse.getStatus().getCode());
        assertEquals("Flight created successfully", flightResponse.getBody().getData().getMessage());
    }

    @Test
    void testDeleteFlight_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.deleteFlight(flightId));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testDeleteFlight_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));

        DefaultMessageResponse defaultMessageResponse = defaultFlightService.deleteFlight(flightId);
        assertEquals("100", defaultMessageResponse.getStatus().getCode());
        assertEquals("Flight deleted successfully", defaultMessageResponse.getBody().getData().getMessage());
    }

    @Test
    void testUpdateFlight_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.updateFlight(flightId, getFlightRequest()));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testUpdateFlight_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));

        DefaultMessageResponse defaultMessageResponse = defaultFlightService.updateFlight(flightId, getFlightRequest());
        assertEquals("100", defaultMessageResponse.getStatus().getCode());
        assertEquals("Flight " + flightId + " updated successfully", defaultMessageResponse.getBody().getData().getMessage());
    }

    @Test
    void testAddSeat_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.addSeat(flightId, getSeatRequest()));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testAddSeat_whenSeatAlreadyExist_returnErrorCode104() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatNameAndFlight(anyString(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.addSeat(flightId, getSeatRequest()));
        assertEquals(104, businessException.getTransactionCode().getId());
    }

    @Test
    void testAddSeat_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatNameAndFlight(anyString(), Mockito.any(Flight.class))).thenReturn(Optional.empty());

        SeatResponse seatResponse = defaultFlightService.addSeat(flightId, getSeatRequest());
        assertEquals("100", seatResponse.getStatus().getCode());
        assertEquals("Seat added to flight " + flightId + " successfully" , seatResponse.getBody().getData().getMessage());
    }

    @Test
    void testDeleteSeat_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.deleteSeat(flightId, seatId));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testDeleteSeat_whenSeatNotExist_returnErrorCode105() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.deleteSeat(flightId, seatId));
        assertEquals(105, businessException.getTransactionCode().getId());
    }

    @Test
    void testDeleteSeat_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));

        DefaultMessageResponse defaultMessageResponse = defaultFlightService.deleteSeat(flightId, seatId);
        assertEquals("100", defaultMessageResponse.getStatus().getCode());
        assertEquals("Seat " + seatId + " deleted successfully", defaultMessageResponse.getBody().getData().getMessage());
    }

    @Test
    void testUpdateSeat_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.updateSeat(flightId, seatId, getSeatRequest()));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testUpdateSeat_whenSeatNotExist_returnErrorCode105() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.updateSeat(flightId, seatId, getSeatRequest()));
        assertEquals(105, businessException.getTransactionCode().getId());
    }

    @Test
    void testUpdateSeat_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));

        DefaultMessageResponse defaultMessageResponse = defaultFlightService.updateSeat(flightId, seatId, getSeatRequest());
        assertEquals("100", defaultMessageResponse.getStatus().getCode());
        assertEquals("Seat " + seatId + " updated for flight " + flightId + " successfully" , defaultMessageResponse.getBody().getData().getMessage());
    }

    @Test
    void testGetAllFlights_whenCalledValid_returnListOfFlights() {
        List<Flight> flightList = new ArrayList<>();
        Flight flight = getFlight();
        Flight flight2 = getFlight();
        flight2.setFlightId(2L);
        flight2.setFlightName("FlightName2");
        Flight flight3 = getFlight();
        flight3.setFlightId(3L);
        flight3.setFlightName("FlightName3");
        flightList.add(flight);
        flightList.add(flight2);
        flightList.add(flight3);

        List<Seat> seatsForFlight = new ArrayList<>();
        Seat seat = getSeat();
        Seat seat2 = getSeat();
        seat2.setSeatId(2L);
        seat2.setSeatName("test2");
        seatsForFlight.add(seat);
        seatsForFlight.add(seat2);

        List<Seat> seatsForFlight2 = new ArrayList<>();
        Seat seat3 = getSeat();
        seat3.setSeatId(3L);
        seatsForFlight2.add(seat3);

        when(flightRepository.findAll()).thenReturn(flightList);
        when(seatRepository.findAllByFlightAndIsReservedFalse(Mockito.any(Flight.class))).thenReturn(seatsForFlight).thenReturn(seatsForFlight2).thenReturn(new ArrayList<>());

        DetailFlightResponse detailFlightResponse = defaultFlightService.getAllFlights();
        assertEquals("100", detailFlightResponse.getStatus().getCode());
        assertEquals(3, detailFlightResponse.getBody().getData().getFlightList().size());
        assertEquals(2, detailFlightResponse.getBody().getData().getFlightList().get(0).getAvaliableSeats().size());
        assertEquals(1, detailFlightResponse.getBody().getData().getFlightList().get(1).getAvaliableSeats().size());
        assertEquals(0, detailFlightResponse.getBody().getData().getFlightList().get(2).getAvaliableSeats().size());
    }

    @Test
    void testBookSeat_whenFlightNotExist_returnErrorCode103() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.bookSeat(flightId, seatId));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testBookSeat_whenSeatNotExist_returnErrorCode105() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.bookSeat(flightId, seatId));
        assertEquals(105, businessException.getTransactionCode().getId());
    }

    @Test
    void testBookSeat_whenSeatAlreadyBooked_returnErrorCode106() {
        Seat seat = getSeat();
        seat.setIsReserved(true);
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(seat));

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.bookSeat(flightId, seatId));
        assertEquals(106, businessException.getTransactionCode().getId());
    }

    @Test
    void testBookSeat_whenPaymentServiceReturnError_returnErrorCode107() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(paymentServiceClients.call(any(BigDecimal.class))).thenReturn(future);

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.bookSeat(flightId, seatId));
        assertEquals(107, businessException.getTransactionCode().getId());
    }

    @Test
    void testBookSeat_whenUniqueConstraintViolationOccurs_returnErrorCode106() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("success");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("test");

        when(paymentServiceClients.call(any(BigDecimal.class))).thenReturn(future);
        doThrow(ex).when(bookedSeatRepository).save(Mockito.any(BookedSeat.class));

        BusinessException businessException = assertThrows(BusinessException.class, () -> defaultFlightService.bookSeat(flightId, seatId));
        assertEquals(106, businessException.getTransactionCode().getId());
    }

    @Test
    void testBookSeat_whenCalledValid_returnSuccess() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(getFlight()));
        when(seatRepository.findBySeatIdAndFlight(anyLong(), Mockito.any(Flight.class))).thenReturn(Optional.of(getSeat()));
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("success");
        when(paymentServiceClients.call(any(BigDecimal.class))).thenReturn(future);

        DefaultMessageResponse defaultMessageResponse = defaultFlightService.bookSeat(flightId, seatId);
        assertEquals("100", defaultMessageResponse.getStatus().getCode());
        assertEquals("Seat " + seatId + " booked successfully", defaultMessageResponse.getBody().getData().getMessage());
    }

    private FlightRequest getFlightRequest() {
        FlightRequest flightRequest = new FlightRequest();
        flightRequest.setFlightName("FlightName");
        flightRequest.setOrigin("test");
        flightRequest.setDestination("test");
        flightRequest.setDepartureTime("22-04-2023 14:30");
        flightRequest.setArrivalTime("22-04-2023 14:30");
        flightRequest.setPrice(BigDecimal.valueOf(100));
        return flightRequest;
    }

    private Flight getFlight() {
        Flight flight = new Flight();
        flight.setFlightId(flightId);
        flight.setFlightName("FlightName");
        flight.setOrigin("test");
        flight.setDestination("test");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        flight.setDepartureTime(LocalDateTime.parse("22-04-2023 14:30", formatter));
        flight.setArrivalTime(LocalDateTime.parse("22-04-2023 14:30", formatter));
        flight.setPrice(BigDecimal.valueOf(100));
        flight.setCreatedAt(LocalDateTime.now());
        flight.setUpdatedAt(LocalDateTime.now());
        return flight;
    }

    private SeatRequest getSeatRequest() {
        SeatRequest seatRequest = new SeatRequest();
        seatRequest.setSeatName("updatedSeatName");
        return seatRequest;
    }

    private Seat getSeat() {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatName("test");
        seat.setIsReserved(false);
        seat.setFlight(getFlight());
        seat.setCreatedAt(LocalDateTime.now());
        seat.setUpdatedAt(LocalDateTime.now());
        return seat;
    }


}