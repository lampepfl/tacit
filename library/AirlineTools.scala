// ─── Data types ─────────────────────────────────────────────────────────────

case class FlightInfo(flightNumber: String, date: String)
case class Passenger(firstName: String, lastName: String, dob: String)
case class Payment(paymentId: String, amount: Int)

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

// ─── Global state ───────────────────────────────────────────────────────────

var _client: MCPClient | Null = null

/** Connect to an airline MCP server. Must be called before using any tool. */
def connect(endpoint: String): Unit =
  if _client != null then _client.nn.close()
  val c = MCPClient(endpoint)
  c.initialize()
  _client = c

private def call(name: String, args: Json = Json.obj()): String =
  val c = _client
  if c == null then throw IllegalStateException("Not connected. Call connect(endpoint) first.")
  val result = c.callTool(name, args)
  if result.isError then
    throw RuntimeException(
      s"Tool '$name' failed: ${result.content.map(_.text).mkString("\n")}"
    )
  result.content.map(_.text).mkString("\n")

// ─── Query tools ──────────────────────────────────────────────────────────

/** Returns a list of all available airports. */
def listAllAirports(): String =
  call("list_all_airports")

/** Get the details of a user, including their reservations. */
def getUserDetails(userId: String): String =
  call("get_user_details", Json.obj(
    "user_id" -> Json.str(userId)
  ))

/** Get the details of a reservation. */
def getReservationDetails(reservationId: String): String =
  call("get_reservation_details", Json.obj(
    "reservation_id" -> Json.str(reservationId)
  ))

/** Get the status of a flight. */
def getFlightStatus(flightNumber: String, date: String): String =
  call("get_flight_status", Json.obj(
    "flight_number" -> Json.str(flightNumber),
    "date" -> Json.str(date)
  ))

/** Search for direct flights between two cities on a specific date. */
def searchDirectFlight(origin: String, destination: String, date: String): String =
  call("search_direct_flight", Json.obj(
    "origin" -> Json.str(origin),
    "destination" -> Json.str(destination),
    "date" -> Json.str(date)
  ))

/** Search for one-stop flights between two cities on a specific date. */
def searchOnestopFlight(origin: String, destination: String, date: String): String =
  call("search_onestop_flight", Json.obj(
    "origin" -> Json.str(origin),
    "destination" -> Json.str(destination),
    "date" -> Json.str(date)
  ))

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
): String =
  call("book_reservation", Json.obj(
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
  ))

/** Cancel the whole reservation. */
def cancelReservation(reservationId: String): String =
  call("cancel_reservation", Json.obj(
    "reservation_id" -> Json.str(reservationId)
  ))

// ─── Update tools ─────────────────────────────────────────────────────────

/** Update the flight information of a reservation. */
def updateReservationFlights(
  reservationId: String,
  cabin: Cabin,
  flights: List[FlightInfo],
  paymentId: String
): String =
  call("update_reservation_flights", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "cabin" -> cabin.toJson,
    "flights" -> Json.arr(flights.map(f => Json.obj(
      "flight_number" -> Json.str(f.flightNumber),
      "date" -> Json.str(f.date)
    ))*),
    "payment_id" -> Json.str(paymentId)
  ))

/** Update the passenger information of a reservation. */
def updateReservationPassengers(
  reservationId: String,
  passengers: List[Passenger]
): String =
  call("update_reservation_passengers", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "passengers" -> Json.arr(passengers.map(p => Json.obj(
      "first_name" -> Json.str(p.firstName),
      "last_name" -> Json.str(p.lastName),
      "dob" -> Json.str(p.dob)
    ))*)
  ))

/** Update the baggage information of a reservation. */
def updateReservationBaggages(
  reservationId: String,
  totalBaggages: Int,
  nonfreeBaggages: Int,
  paymentId: String
): String =
  call("update_reservation_baggages", Json.obj(
    "reservation_id" -> Json.str(reservationId),
    "total_baggages" -> Json.num(totalBaggages),
    "nonfree_baggages" -> Json.num(nonfreeBaggages),
    "payment_id" -> Json.str(paymentId)
  ))

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
