package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatNameAndFlight(String seatName, Flight flight);

    Optional<Seat> findBySeatIdAndFlight(Long seatId, Flight flight);

    List<Seat> findAllByFlightAndIsReservedFalse(Flight flight);
}
