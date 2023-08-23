package com.iyzico.challenge.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seatId", "flightId"})
})
@Entity
@Getter
@Setter
public class BookedSeat {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "seatId")
    private Seat seat;
    @ManyToOne
    @JoinColumn(name = "flightId")
    private Flight flight;
    @CreationTimestamp
    private LocalDateTime bookedAt;
}
