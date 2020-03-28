package com.ticketbooking.service;

import com.ticketbooking.dto.*;
import com.ticketbooking.exception.TicketBookingNotFoundException;
import com.ticketbooking.model.*;
import com.ticketbooking.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm";

    private MailService mailService;

    private MovieRepo movieRepo;
    private ScreeningRepo screeningRepo;
    private RoomRepo roomRepo;
    private SeatRepo seatRepo;
    private TicketRepo ticketRepo;
    private ReservationRepo reservationRepo;

    @Autowired
    public ReservationService(MovieRepo movieRepo, ScreeningRepo screeningRepo,
                              ReservationRepo reservationRepo, RoomRepo roomRepo,
                              SeatRepo seatRepo, TicketRepo ticketRepo, MailService mailService) {
        this.movieRepo = movieRepo;
        this.screeningRepo = screeningRepo;
        this.roomRepo = roomRepo;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.reservationRepo = reservationRepo;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getMovies(String dateFrom, String dateTo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        LocalDateTime formatDateFrom = LocalDateTime.parse(dateFrom, formatter);
        LocalDateTime formatDateTo = LocalDateTime.parse(dateTo, formatter);

        List<Screening> screenings = screeningRepo.findByScreeningTimeBetween(formatDateFrom, formatDateTo);

        List<MovieDto> movies = screenings.stream()
                .map(s -> {
                    Optional<Movie> movie = movieRepo.findById(s.getMovie().getId());
                    if (movie.isPresent()) {
                        MovieDto movieDto = new MovieDto(movie.get().getId(), s.getId(), movie.get().getTitle(), s.getScreeningTime());
                        return movieDto;
                    } else {
                        return null;
                    }
                })
                .filter(m -> m != null)
                .sorted(Comparator.comparing(MovieDto::getMovieTitle).thenComparing(MovieDto::getDateTimeMovie))
                .collect(Collectors.toList());
        return  movies;
    }

    @Transactional(readOnly = true)
    public ScreeningDto getScreening(Long screeningId) {

        ScreeningDto screeningDto = getScreeningDto(screeningId);
        filterBusySeats(screeningId, screeningDto);

        return screeningDto;
    }

    private void filterBusySeats(Long screeningId, ScreeningDto screeningDto) {
        List<Long> busySeatsList = ticketRepo.findAllReservedByScreeningId(screeningId);

        List<SeatDto> filteredList = screeningDto.getSeats()
                .stream()
                .filter(s -> !busySeatsList.contains(s.getId()))
                .collect(Collectors.toList());

        screeningDto.setSeats(filteredList);
    }

    private ScreeningDto getScreeningDto(Long screeningId) {
        ScreeningDto screeningDto = new ScreeningDto();

        Screening screening = screeningRepo.findById(screeningId).orElseThrow(
                () -> new TicketBookingNotFoundException("Błąd: Seans " + String.valueOf(screeningId)));

        screeningDto.setDateTimeMovie(screening.getScreeningTime());
        Movie movie = movieRepo.findById(screening.getMovie().getId()).orElseThrow(
                () -> new TicketBookingNotFoundException("Błąd: Film " + String.valueOf(screening.getMovie().getId())));

        screeningDto.setMovieTitle(movie.getTitle());
        Room room = roomRepo.findById(screening.getRoom().getId()).orElseThrow(
                () -> new TicketBookingNotFoundException("Błąd: Sala " + String.valueOf(screening.getRoom().getId())));

        screeningDto.setRoomNumber(room.getRoomNumber());
        List<Seat> seatAllList = seatRepo.findAllByRoomId(room.getId());

        List<SeatDto> seatDtos = seatAllList.stream()
                .map(s -> {
                    SeatDto seatDto = new SeatDto
                            (s.getId(), s.getRowNr(), s.getSeatNr(), s.getRoom().getId());
                    return seatDto;
                })
                .collect(Collectors.toList());

        screeningDto.setSeats(seatDtos);

        return screeningDto;
    }

    @Transactional
    public String makeReservation(ReservationDto reservationDto, HttpServletRequest request) {
        StringBuilder response = new StringBuilder();
        Screening screening = screeningRepo.findById(reservationDto.getScreeningId()).orElseThrow(
                () -> new TicketBookingNotFoundException("Błąd: Seans  " + String.valueOf(reservationDto.getScreeningId())));

        if (LocalDateTime.now().plusMinutes(15).isAfter(screening.getScreeningTime())) {
            response.append("<H1>Upłynął czas na dokonanie rezerwacji</H1>");
        } else if (reservationDto.getTickets().isEmpty()) {
            response.append("<H1>Rezerwacja powinna dotyczyć przynajmniej jednego miejsca</H1>");
        } else if (!checkSingleSeat(reservationDto, screening)) {
            response.append("<H1>Rezerwowane miejsca powinny znajdować się obok wcześniej zarezerwowanych</H1>");
        } else {
            Reservation reservation = saveReservation(reservationDto, screening);
            BigDecimal ticketsPrice = saveTickets(reservationDto, screening, reservation);

            sendReservationByMail(request, reservation.getId(), reservationDto.getMail());
            
            response.append("<H1>Potwierdzenie rezerwacji</H1>");
            response.append("<p>Rezerwacja dokonana na nazwisko:" + reservationDto.getName() + " " + reservationDto.getSurname() + "</p>");
            response.append("<p>Kwota do zapłaty : " + ticketsPrice + "</p>");
            response.append("<p>Potwierdzenie rezerwacji powinno być dokonane w ciągu 15 minut poprzez kliknięcie w link wysłany na podany adres mailowy.</p>");
        }

        return response.toString();
    }

    private void sendReservationByMail(HttpServletRequest request, Long reservationId, String mail) {
        try {
            String url = "http://" +
                    request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
                    "/confirmReservation?reservationId=" + reservationId;
            mailService.sendMail(mail, "Rezerwacja biletów", url, false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private boolean checkSingleSeat(ReservationDto reservationDto, Screening screening) {
        Map<Integer, List<Long>> map = getSeatsForScreening(reservationDto, screening);

        map.entrySet().stream().forEach(System.out::println);

        return verificationSeatsAfterReservation(map);
    }

    private boolean verificationSeatsAfterReservation(Map<Integer,List<Long>> map) {

        final boolean[] verify = { true };
        map.entrySet().stream()
            .forEach(k -> {
                        verify[0] = verifyPlacesInRow(k.getValue(), verify[0]);
                    });

        return verify[0];
    }

    private boolean verifyPlacesInRow(List<Long> row, boolean verify) {

        if (!verify) return verify;
        for (int ii = 1; ii < row.size()-1; ii++) {
            if ( (row.get(ii) != 0) && (row.get(ii-1) == 0) && (row.get(ii+1) == 0)) {
                verify = false;
            }
        }

        return verify;
    }

    private Map<Integer, List<Long>> getSeatsForScreening(ReservationDto reservationDto, Screening screening) {
        List<Seat> allSeatList = seatRepo.findAllByRoomId(screening.getRoom().getId());

        Map<Integer, List<Long>> map = allSeatList.stream().collect(
            Collectors.groupingBy(Seat::getRowNr, Collectors.mapping(Seat::getId, Collectors.toList())));

        setReservedSeats(ticketRepo.findAllReservedByScreeningId(screening.getId()), map);
        setReservedSeats(setReservedSeatsForCurrentReservation(reservationDto.getTickets()), map);

        return map;
    }

    private List<Long> setReservedSeatsForCurrentReservation(List<TicketDto> tickets) {

        return tickets.stream()
            .map(t -> t.getSeatId())
            .collect(Collectors.toList());
    }

    private void setReservedSeats(List<Long> seatList, Map<Integer, List<Long>> map) {
        final int[] idx = { 0 };
        map.entrySet().stream()
            .map(v -> {
                idx[0] = 0;
                return v;
            })
            .forEach(k -> k.getValue().stream()
                .forEach(v -> {
                    if (seatList.contains(v)) {
                        k.getValue().set(idx[0], 0L);
                    }
                    idx[0]++;
                }));
    }

    private BigDecimal saveTickets(ReservationDto reservationDto, Screening screening, Reservation reservation) {
        final BigDecimal[] sum = {BigDecimal.ZERO};
        reservationDto.getTickets().stream()
                .map(r -> {
                    Optional<Seat> seat = seatRepo.findById(r.getSeatId());

                    Ticket ticket = new Ticket();
                    ticket.setTicketType(r.getTicketType());
                    ticket.setReservation(reservation);
                    ticket.setScreening(screening);
                    if (seat.isPresent()) {  ticket.setSeat(seat.get()); }
                    return ticket;
                })
                .filter(t -> t.getSeat() != null)
                .map(t -> {
                    sum[0] = sum[0].add(t.getTicketType().getTicketPrice());
                    return t;
                })
                .forEach(t -> ticketRepo.save(t));

        return sum[0];
    }

    private Reservation saveReservation(ReservationDto reservationDto, Screening screening) {
        Reservation reservation = new Reservation();
        reservation.setUserName(reservationDto.getName());
        reservation.setUserSurname(reservationDto.getSurname());
        reservation.setScreening(screening);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setConfirmed(Boolean.FALSE);

        reservationRepo.save(reservation);
        return reservation;
    }

    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId).orElseThrow(
                () -> new TicketBookingNotFoundException("Błąd: Rezerwacja  " + String.valueOf(reservationId)));
        reservation.setConfirmed(true);

        reservationRepo.save(reservation);
    }
}
