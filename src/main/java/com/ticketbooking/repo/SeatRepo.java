package com.ticketbooking.repo;

import com.ticketbooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {

    List<Seat> findAllByRoomId(Long roomId);
}
