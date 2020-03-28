package com.ticketbooking.dto;

public class SeatDto {
    private Long id;
    private Integer rowNr;
    private Integer seatNr;
    private Long roomId;

    public SeatDto(Long id, Integer rowNr, Integer seatNr, Long roomId) {
        this.id = id;
        this.rowNr = rowNr;
        this.seatNr = seatNr;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public Integer getRowNr() {
        return rowNr;
    }

    public Integer getSeatNr() {
        return seatNr;
    }

    public Long getRoomId() {
        return roomId;
    }
}
