package com.ticketbooking.repo;

import com.ticketbooking.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {

    @Query("select t.seat.id from Ticket t join t.reservation r join t.screening s " +
            "where t.screening.id = :screeningId " +
            "and (r.confirmed = true " +
            "or (r.reservationTime between (SYSTIMESTAMP - 0.010416) AND SYSTIMESTAMP and SYSTIMESTAMP < s.screeningTime - 0.010416))")
    List<Long> findAllReservedByScreeningId(@Param("screeningId") Long screeningId);
}
