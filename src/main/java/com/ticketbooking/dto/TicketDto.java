package com.ticketbooking.dto;

import com.ticketbooking.model.enums.TicketType;

public class TicketDto {

    private TicketType ticketType;
    private Long seatId;

    public TicketDto() {
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }
}
