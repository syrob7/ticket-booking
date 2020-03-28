package com.ticketbooking.model;

import javax.persistence.*;

@Entity
@Table(name="seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rowNr;
    private Integer seatNr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Seat() {
    }

    public Seat(Integer rowNr, Integer seatNr) {
        this.rowNr = rowNr;
        this.seatNr = seatNr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRowNr() {
        return rowNr;
    }

    public void setRowNr(Integer rowNr) {
        this.rowNr = rowNr;
    }

    public Integer getSeatNr() {
        return seatNr;
    }

    public void setSeatNr(Integer seatNr) {
        this.seatNr = seatNr;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
