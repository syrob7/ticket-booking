package com.ticketbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScreeningDto {

    private String movieTitle;
    private LocalDateTime dateTimeMovie;
    private String roomNumber;
    private List<SeatDto> seats;

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDateTime getDateTimeMovie() {
        return dateTimeMovie;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setDateTimeMovie(LocalDateTime dateTimeMovie) {
        this.dateTimeMovie = dateTimeMovie;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seats = seats;
    }
}
