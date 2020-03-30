# ticket-booking

Ticket booking app - install and run application - excecute installAndRunApp.bat file

Ticket booking app - curl scenarios

#scenario no. 1 - get a list of available screenings
curl -X GET -H "Content-type: application/json" -H "Accept: application/json" "http://localhost:8080/movies?dateFrom=2020-04-04%2012:00&dateTo=2020-04-05%2020:00"

#scenario no. 2 - get details screenings no. 1
curl -X GET -H "Content-type: application/json" -H "Accept: application/json" "http://localhost:8080/screening?language=pl&screeningId=1"

#scenario no. 3 - attempt to make a reservation without tickets
curl -X  POST  -H "Content-Type: application/json"  -d @.\src\main\curl\no-tickets.json http://localhost:8080/reservation?language=pl

#scenario no. 4 - attempt to make a reservation with wrong name
curl -X  POST  -H "Content-Type: application/json"  -d @.\src\main\curl\wrong-name.json http://localhost:8080/reservation?language=pl

#scenario no. 5 - attempt to make a reservation with wrong places
curl -X  POST  -H "Content-Type: application/json"  -d @.\src\main\curl\bad-seats.json http://localhost:8080/reservation?language=pl

#scenario no. 6 - make a reservation with proper data
curl -X  POST  -H "Content-Type: application/json"  -d @.\src\main\curl\make-reservation.json http://localhost:8080/reservation?language=pl

#scenario no. 7 - reservation confirmation
curl -X GET -H "Content-type: application/json" -H "Accept: application/json" "http://localhost:8080/confirmReservation?reservationId=3&language=pl"

