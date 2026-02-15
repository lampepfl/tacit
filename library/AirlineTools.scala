package library
package facade.airline

import mcpclient.*
import language.experimental.captureChecking

// ─── Data types ─────────────────────────────────────────────────────────────

case class FlightInfo(flightNumber: String, date: String)

case class Passenger(firstName: String, lastName: String, dob: String)
object Passenger:
  def fromJson(j: Json): Passenger = Passenger(
    firstName = j("first_name").asString.getOrElse(""),
    lastName = j("last_name").asString.getOrElse(""),
    dob = j("dob").asString.getOrElse("")
  )

case class Payment(paymentId: String, amount: Int)
object Payment:
  def fromJson(j: Json): Payment = Payment(
    paymentId = j("payment_id").asString.getOrElse(""),
    amount = j("amount").asInt.getOrElse(0)
  )

enum FlightType:
  case RoundTrip, OneWay
  def toJson: Json = this match
    case RoundTrip => Json.str("round_trip")
    case OneWay    => Json.str("one_way")

enum Cabin:
  case Business, Economy, BasicEconomy
  def toJson: Json = this match
    case Business     => Json.str("business")
    case Economy      => Json.str("economy")
    case BasicEconomy => Json.str("basic_economy")

enum Insurance:
  case Yes, No
  def toJson: Json = this match
    case Yes => Json.str("yes")
    case No  => Json.str("no")

case class AirportCode(iata: String, city: String)
object AirportCode:
  def fromJson(j: Json): AirportCode = AirportCode(
    iata = j("iata").asString.getOrElse(""),
    city = j("city").asString.getOrElse("")
  )

case class Name(firstName: String, lastName: String)
object Name:
  def fromJson(j: Json): Name = Name(
    firstName = j("first_name").asString.getOrElse(""),
    lastName = j("last_name").asString.getOrElse("")
  )

case class Address(
  address1: String,
  address2: Option[String],
  city: String,
  country: String,
  state: String,
  zip: String
)
object Address:
  def fromJson(j: Json): Address = Address(
    address1 = j("address1").asString.getOrElse(""),
    address2 = j("address2").asString,
    city = j("city").asString.getOrElse(""),
    country = j("country").asString.getOrElse(""),
    state = j("state").asString.getOrElse(""),
    zip = j("zip").asString.getOrElse("")
  )

enum PaymentMethod:
  case CreditCard(id: String, brand: String, lastFour: String)
  case GiftCard(id: String, amount: Double)
  case Certificate(id: String, amount: Double)

object PaymentMethod:
  def fromJson(j: Json): PaymentMethod =
    j("source").asString.getOrElse("") match
      case "credit_card" => CreditCard(
        id = j("id").asString.getOrElse(""),
        brand = j("brand").asString.getOrElse(""),
        lastFour = j("last_four").asString.getOrElse("")
      )
      case "gift_card" => GiftCard(
        id = j("id").asString.getOrElse(""),
        amount = j("amount").asDouble.getOrElse(0.0)
      )
      case "certificate" => Certificate(
        id = j("id").asString.getOrElse(""),
        amount = j("amount").asDouble.getOrElse(0.0)
      )
      case other => throw IllegalArgumentException(s"Unknown payment source: $other")

case class ReservationFlight(
  flightNumber: String,
  origin: String,
  destination: String,
  date: String,
  price: Int
)
object ReservationFlight:
  def fromJson(j: Json): ReservationFlight = ReservationFlight(
    flightNumber = j("flight_number").asString.getOrElse(""),
    origin = j("origin").asString.getOrElse(""),
    destination = j("destination").asString.getOrElse(""),
    date = j("date").asString.getOrElse(""),
    price = j("price").asInt.getOrElse(0)
  )

case class User(
  userId: String,
  name: Name,
  address: Address,
  email: String,
  dob: String,
  paymentMethods: Map[String, PaymentMethod],
  savedPassengers: List[Passenger],
  membership: String,
  reservations: List[String]
)
object User:
  def fromJson(j: Json): User = User(
    userId = j("user_id").asString.getOrElse(""),
    name = Name.fromJson(j("name")),
    address = Address.fromJson(j("address")),
    email = j("email").asString.getOrElse(""),
    dob = j("dob").asString.getOrElse(""),
    paymentMethods = decodeMap(j("payment_methods"), PaymentMethod.fromJson),
    savedPassengers = j("saved_passengers").asArray.getOrElse(Nil).map(Passenger.fromJson),
    membership = j("membership").asString.getOrElse(""),
    reservations = j("reservations").asArray.getOrElse(Nil).flatMap(_.asString)
  )

case class Reservation(
  reservationId: String,
  userId: String,
  origin: String,
  destination: String,
  flightType: String,
  cabin: String,
  flights: List[ReservationFlight],
  passengers: List[Passenger],
  paymentHistory: List[Payment],
  createdAt: String,
  totalBaggages: Int,
  nonfreeBaggages: Int,
  insurance: String,
  status: Option[String]
)
object Reservation:
  def fromJson(j: Json): Reservation = Reservation(
    reservationId = j("reservation_id").asString.getOrElse(""),
    userId = j("user_id").asString.getOrElse(""),
    origin = j("origin").asString.getOrElse(""),
    destination = j("destination").asString.getOrElse(""),
    flightType = j("flight_type").asString.getOrElse(""),
    cabin = j("cabin").asString.getOrElse(""),
    flights = j("flights").asArray.getOrElse(Nil).map(ReservationFlight.fromJson),
    passengers = j("passengers").asArray.getOrElse(Nil).map(Passenger.fromJson),
    paymentHistory = j("payment_history").asArray.getOrElse(Nil).map(Payment.fromJson),
    createdAt = j("created_at").asString.getOrElse(""),
    totalBaggages = j("total_baggages").asInt.getOrElse(0),
    nonfreeBaggages = j("nonfree_baggages").asInt.getOrElse(0),
    insurance = j("insurance").asString.getOrElse(""),
    status = j("status").asString
  )

case class DirectFlight(
  flightNumber: String,
  origin: String,
  destination: String,
  status: String,
  scheduledDepartureTimeEst: String,
  scheduledArrivalTimeEst: String,
  date: Option[String],
  availableSeats: Map[String, Int],
  prices: Map[String, Int]
)
object DirectFlight:
  def fromJson(j: Json): DirectFlight = DirectFlight(
    flightNumber = j("flight_number").asString.getOrElse(""),
    origin = j("origin").asString.getOrElse(""),
    destination = j("destination").asString.getOrElse(""),
    status = j("status").asString.getOrElse(""),
    scheduledDepartureTimeEst = j("scheduled_departure_time_est").asString.getOrElse(""),
    scheduledArrivalTimeEst = j("scheduled_arrival_time_est").asString.getOrElse(""),
    date = j("date").asString,
    availableSeats = decodeMap(j("available_seats"), _.asInt.getOrElse(0)),
    prices = decodeMap(j("prices"), _.asInt.getOrElse(0))
  )

private def decodeMap[V](j: Json, f: Json => V): Map[String, V] =
  j.asObject match
    case Some(fields) => fields.map((k, v) => k -> f(v)).toMap
    case None => Map.empty

// ─── Global state ───────────────────────────────────────────────────────────

var _client: MCPClient | Null = null

/** Connect to an airline MCP server. Must be called before using any tool. */
def connect(endpoint: String): Unit =
  if _client != null then _client.nn.close()
  val c = MCPClient(endpoint)
  c.initialize()
  _client = c

private def call(name: String, args: Json = Json.obj()): String =
  callRaw(name, args).mkString("\n")

private def callRaw(name: String, args: Json = Json.obj()): List[String] =
  val c = _client
  if c == null then throw IllegalStateException("Not connected. Call connect(endpoint) first.")
  val result = c.callTool(name, args)
  if result.isError then
    throw RuntimeException(
      s"Tool '$name' failed: ${result.content.map(_.text).mkString("\n")}"
    )
  result.content.map(_.text)

private def callJson(name: String, args: Json = Json.obj()): Json =
  val blocks = callRaw(name, args)
  if blocks.size == 1 then Json.parse(blocks.head)
  else Json.JArr(blocks.map(Json.parse))

// ─── Query tools ──────────────────────────────────────────────────────────

/** Returns a list of all available airports. */
def listAllAirports(): List[AirportCode] =
  callJson("list_all_airports").asArray.getOrElse(Nil).map(AirportCode.fromJson)

/** Get the details of a user, including their reservations. */
def getUserDetails(userId: String): User =
  User.fromJson(callJson("get_user_details", Json.obj(
    "user_id" -> Json.str(userId)
  )))

/** Get the details of a reservation. */
def getReservationDetails(reservationId: String): Reservation =
  Reservation.fromJson(callJson("get_reservation_details", Json.obj(
    "reservation_id" -> Json.str(reservationId)
  )))

/** Get the status of a flight. */
def getFlightStatus(flightNumber: String, date: String): String =
  call("get_flight_status", Json.obj(
    "flight_number" -> Json.str(flightNumber),
    "date" -> Json.str(date)
  ))

/** Search for direct flights between two cities on a specific date. */
def searchDirectFlight(origin: String, destination: String, date: String): List[DirectFlight] =
  callJson("search_direct_flight", Json.obj(
    "origin" -> Json.str(origin),
    "destination" -> Json.str(destination),
    "date" -> Json.str(date)
  )).asArray.getOrElse(Nil).map(DirectFlight.fromJson)

/** Search for one-stop flights between two cities on a specific date. */
def searchOnestopFlight(origin: String, destination: String, date: String): List[List[DirectFlight]] =
  callJson("search_onestop_flight", Json.obj(
    "origin" -> Json.str(origin),
    "destination" -> Json.str(destination),
    "date" -> Json.str(date)
  )).asArray.getOrElse(Nil).map(_.asArray.getOrElse(Nil).map(DirectFlight.fromJson))

/** Calculate the result of a mathematical expression. */
def calculate(expression: String): String =
  call("calculate", Json.obj(
    "expression" -> Json.str(expression)
  ))

// ─── Booking tools ────────────────────────────────────────────────────────

/** Book a reservation. */
def bookReservation(
  userId: String,
  origin: String,
  destination: String,
  flightType: FlightType,
  cabin: Cabin,
  flights: List[FlightInfo],
  passengers: List[Passenger],
  paymentMethods: List[Payment],
  totalBaggages: Int,
  nonfreeBaggages: Int,
  insurance: Insurance
): Reservation =
  Reservation.fromJson(callJson("book_reservation", Json.obj(
    "user_id" -> Json.str(userId),
    "origin" -> Json.str(origin),
    "destination" -> Json.str(destination),
    "flight_type" -> flightType.toJson,
    "cabin" -> cabin.toJson,
    "flights" -> Json.arr(flights.map(f => Json.obj(
      "flight_number" -> Json.str(f.flightNumber),
      "date" -> Json.str(f.date)
    ))*),
    "passengers" -> Json.arr(passengers.map(p => Json.obj(
      "first_name" -> Json.str(p.firstName),
      "last_name" -> Json.str(p.lastName),
      "dob" -> Json.str(p.dob)
    ))*),
    "payment_methods" -> Json.arr(paymentMethods.map(pm => Json.obj(
      "payment_id" -> Json.str(pm.paymentId),
      "amount" -> Json.num(pm.amount)
    ))*),
    "total_baggages" -> Json.num(totalBaggages),
    "nonfree_baggages" -> Json.num(nonfreeBaggages),
    "insurance" -> insurance.toJson
  )))

/** Cancel the whole reservation. */
def cancelReservation(reservationId: String): Reservation =
  Reservation.fromJson(callJson("cancel_reservation", Json.obj(
    "reservation_id" -> Json.str(reservationId)
  )))

// ─── Update tools ─────────────────────────────────────────────────────────

/** Update the flight information of a reservation. */
def updateReservationFlights(
  reservationId: String,
  cabin: Cabin,
  flights: List[FlightInfo],
  paymentId: String
): Reservation =
  Reservation.fromJson(callJson("update_reservation_flights", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "cabin" -> cabin.toJson,
    "flights" -> Json.arr(flights.map(f => Json.obj(
      "flight_number" -> Json.str(f.flightNumber),
      "date" -> Json.str(f.date)
    ))*),
    "payment_id" -> Json.str(paymentId)
  )))

/** Update the passenger information of a reservation. */
def updateReservationPassengers(
  reservationId: String,
  passengers: List[Passenger]
): Reservation =
  Reservation.fromJson(callJson("update_reservation_passengers", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "passengers" -> Json.arr(passengers.map(p => Json.obj(
      "first_name" -> Json.str(p.firstName),
      "last_name" -> Json.str(p.lastName),
      "dob" -> Json.str(p.dob)
    ))*)
  )))

/** Update the baggage information of a reservation. */
def updateReservationBaggages(
  reservationId: String,
  totalBaggages: Int,
  nonfreeBaggages: Int,
  paymentId: String
): Reservation =
  Reservation.fromJson(callJson("update_reservation_baggages", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "total_baggages" -> Json.num(totalBaggages),
    "nonfree_baggages" -> Json.num(nonfreeBaggages),
    "payment_id" -> Json.str(paymentId)
  )))

// ─── Other tools ──────────────────────────────────────────────────────────

/** Send a certificate to a user. */
def sendCertificate(userId: String, amount: Int): String =
  call("send_certificate", Json.obj(
    "user_id" -> Json.str(userId),
    "amount" -> Json.num(amount)
  ))

/** Transfer the user to a human agent with a summary. */
def transferToHumanAgents(summary: String): String =
  call("transfer_to_human_agents", Json.obj(
    "summary" -> Json.str(summary)
  ))
