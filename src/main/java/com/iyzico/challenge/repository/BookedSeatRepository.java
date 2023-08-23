package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {
}
