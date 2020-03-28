package com.ticketbooking.exception;

public class TicketBookingNotFoundException extends RuntimeException {
    public TicketBookingNotFoundException(String value) {
        super(value + " nie znaleziono");
    }
}
