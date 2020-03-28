package com.ticketbooking.dto;

import java.time.LocalDateTime;

public class MovieDto {
    private Long movieId;
    private Long screeningId;
    private String movieTitle;
    private LocalDateTime dateTimeMovie;

    public MovieDto(Long movieId, Long screeningId, String movieTitle, LocalDateTime dateTimeMovie) {
        this.movieId = movieId;
        this.screeningId = screeningId;
        this.movieTitle = movieTitle;
        this.dateTimeMovie = dateTimeMovie;
    }

    public Long getMovieId() {
        return movieId;
    }

    public Long getScreeningId() {
        return screeningId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDateTime getDateTimeMovie() {
        return dateTimeMovie;
    }


}
