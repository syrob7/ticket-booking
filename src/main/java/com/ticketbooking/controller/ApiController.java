package com.ticketbooking.controller;

import com.ticketbooking.dto.MovieDto;
import com.ticketbooking.dto.ReservationDto;
import com.ticketbooking.dto.ScreeningDto;
import com.ticketbooking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ApiController {

    private ReservationService reservationService;

    @Autowired
    public ApiController(ReservationService service) {
        this.reservationService = service;
    }

    @GetMapping("/movies")
    public List<MovieDto> getMovies(@RequestParam String dateFrom, @RequestParam String dateTo) {
        return reservationService.getMovies(dateFrom, dateTo);
    }

    @GetMapping("/screening")
    public ScreeningDto getScreeningInfo(@RequestParam Long screeningId) {
        return reservationService.getScreening(screeningId);
    }

    @PostMapping(value = "/reservation", consumes = "application/json")
    public String makeReservation(@RequestBody ReservationDto reservationDto, HttpServletRequest request) {
        return reservationService.makeReservation(reservationDto, request);
    }

    @GetMapping(value = "/confirmReservation")
    public String confirmReservation(@RequestParam Long reservationId) {
        return reservationService.confirmReservation(reservationId);
    }
}
