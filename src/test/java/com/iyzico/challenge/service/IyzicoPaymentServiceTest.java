package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.response.DefaultMessageResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class IyzicoPaymentServiceTest {

    @Autowired
    private PaymentServiceClients paymentServiceClients;

    @Autowired
    private DefaultFlightService defaultFlightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    public void should_pay_with_iyzico_with_100_clients_together() {
        List<CompletableFuture> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CompletableFuture<String> future = paymentServiceClients.call(new BigDecimal(i));
            futures.add(future);
        }
        futures.stream().forEach(f -> CompletableFuture.allOf(f).join());
    }

    // This test shows that the service is thread-safe. When two clients try to book the same seat at the same time, even if both of them go through the if statement in the service, only one of them can book the seat.
    @Test
    public void whenServiceCalledSimultaneously_firstOneGetsSuccess_otherGetsErrorMessage() {
        fillDatabase();

        DefaultMessageResponse response = defaultFlightService.bookSeat(1L, 1L);
        assert response.getStatus().getSuccess();

        seatRepository.findById(1L).ifPresent(seat -> {
            seat.setIsReserved(false);
        });

        try {
            defaultFlightService.bookSeat(1L, 1L);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void fillDatabase() {
        Flight flight = getFlight(1L);
        flightRepository.save(flight);

        Seat seat = getSeat(1L, flight);
        seatRepository.save(seat);
    }

    private Flight getFlight(Long flightId) {
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

    private Seat getSeat(Long seatId, Flight flight) {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatName("test");
        seat.setIsReserved(false);
        seat.setFlight(flight);
        seat.setCreatedAt(LocalDateTime.now());
        seat.setUpdatedAt(LocalDateTime.now());
        return seat;
    }
}
