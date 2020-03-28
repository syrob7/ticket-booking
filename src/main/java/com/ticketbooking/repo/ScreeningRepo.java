package com.ticketbooking.repo;

import com.ticketbooking.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreeningRepo extends JpaRepository<Screening, Long> {

    List<Screening> findByScreeningTimeBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

}
