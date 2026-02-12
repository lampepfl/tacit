# Airline Tools API Reference

## Data Types

```scala
case class FlightInfo(flightNumber: String, date: String)  // date: "YYYY-MM-DD", e.g. "2024-05-01"
case class Passenger(firstName: String, lastName: String, dob: String)  // dob: "YYYY-MM-DD"
case class Payment(paymentId: String, amount: Int)  // amount in dollars

enum FlightType { case RoundTrip, OneWay }
enum Cabin { case Business, Economy, BasicEconomy }
enum Insurance { case Yes, No }
```

## Query Tools

| Method | Description |
|--------|-------------|
| `listAllAirports(): String` | Returns a list of all available airports. |
| `getUserDetails(userId: String): String` | Get the details of a user, including their reservations. |
| `getReservationDetails(reservationId: String): String` | Get the details of a reservation. |
| `getFlightStatus(flightNumber: String, date: String): String` | Get the status of a flight. |
| `searchDirectFlight(origin: String, destination: String, date: String): String` | Search for direct flights between two cities on a specific date. |
| `searchOnestopFlight(origin: String, destination: String, date: String): String` | Search for one-stop flights between two cities on a specific date. |
| `calculate(expression: String): String` | Calculate the result of a mathematical expression. |

## Booking Tools

| Method | Description |
|--------|-------------|
| `bookReservation(userId: String, origin: String, destination: String, flightType: FlightType, cabin: Cabin, flights: List[FlightInfo], passengers: List[Passenger], paymentMethods: List[Payment], totalBaggages: Int, nonfreeBaggages: Int, insurance: Insurance): String` | Book a reservation. |
| `cancelReservation(reservationId: String): String` | Cancel the whole reservation. |

## Update Tools

| Method | Description |
|--------|-------------|
| `updateReservationFlights(reservationId: String, cabin: Cabin, flights: List[FlightInfo], paymentId: String): String` | Update the flight information of a reservation. |
| `updateReservationPassengers(reservationId: String, passengers: List[Passenger]): String` | Update the passenger information of a reservation. |
| `updateReservationBaggages(reservationId: String, totalBaggages: Int, nonfreeBaggages: Int, paymentId: String): String` | Update the baggage information of a reservation. |

## Other Tools

| Method | Description |
|--------|-------------|
| `sendCertificate(userId: String, amount: Int): String` | Send a certificate to a user. Be careful! |
| `transferToHumanAgents(summary: String): String` | Transfer the user to a human agent, with a summary of the user's issue. Only transfer if the user explicitly asks for a human agent, or given the policy and the available tools, you cannot solve the user's issue. |
