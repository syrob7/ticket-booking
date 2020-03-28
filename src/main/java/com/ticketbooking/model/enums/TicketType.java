package com.ticketbooking.model.enums;

import java.math.BigDecimal;

public enum TicketType {
    ADULT(new BigDecimal("25")),
    STUDENT(new BigDecimal("18")),
    CHILD(new BigDecimal("12.50"));

    TicketType(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    private BigDecimal ticketPrice;

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }
}
