package tacit.library.travel

import language.experimental.captureChecking
import caps.*

import tacit.library.{Classified, ClassifiedImpl, LlmConfig, LlmOps}
import tacit.library.mcp.{JValue, MCPClient, MCPError, TextParsers}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}
import scala.util.matching.Regex

@assumeSafe
class TravelImpl(endpoint: String, secureOutputPath: String) extends TravelService, AutoCloseable:
  private val client = MCPClient(endpoint)

  client.initialize()
  client.sendInitialized()

  def close(): Unit = client.close()

  def getUserInformation(): UserInformation =
    parseUserInformation(callToolRepr("get_user_information", JValue.obj()))

  def getAllHotelsInCity(city: String): List[String] =
    parseLabeledLines(callToolText("get_all_hotels_in_city", JValue.obj("city" -> JValue.str(city))))

  def getHotelsPrices(hotelNames: List[String]): Map[String, PriceRange] =
    parsePriceRangeMap(callToolRepr("get_hotels_prices", JValue.obj(
      "hotel_names" -> JValue.arr(hotelNames.map(JValue.str)*)
    )))

  def getRatingReviewsForHotels(hotelNames: List[String]): Classified[Map[String, RatedReviews]] =
    ClassifiedImpl.wrap:
      parseRatedReviewsMap(callToolRepr("get_rating_reviews_for_hotels", JValue.obj(
        "hotel_names" -> JValue.arr(hotelNames.map(JValue.str)*)
      )))

  def getHotelsAddress(hotelName: String): Option[String] =
    callToolRepr("get_hotels_address", JValue.obj("hotel_name" -> JValue.str(hotelName)))
      .field(hotelName).asString

  def getAllRestaurantsInCity(city: String): List[String] =
    parseLabeledLines(callToolText("get_all_restaurants_in_city", JValue.obj("city" -> JValue.str(city))))

  def getCuisineTypeForRestaurants(restaurantNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("get_cuisine_type_for_restaurants", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def getRestaurantsAddress(restaurantNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("get_restaurants_address", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def getRatingReviewsForRestaurants(restaurantNames: List[String]): Classified[Map[String, RatedReviews]] =
    ClassifiedImpl.wrap:
      parseRatedReviewsMap(callToolRepr("get_rating_reviews_for_restaurants", JValue.obj(
        "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
      )))

  def getDietaryRestrictionsForAllRestaurants(restaurantNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("get_dietary_restrictions_for_all_restaurants", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def getContactInformationForRestaurants(restaurantNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("get_contact_information_for_restaurants", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def getPriceForRestaurants(restaurantNames: List[String]): Map[String, Double] =
    parseDoubleMap(callToolRepr("get_price_for_restaurants", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def checkRestaurantOpeningHours(restaurantNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("check_restaurant_opening_hours", JValue.obj(
      "restaurant_names" -> JValue.arr(restaurantNames.map(JValue.str)*)
    )))

  def getAllCarRentalCompaniesInCity(city: String): List[String] =
    parseLabeledLines(callToolText("get_all_car_rental_companies_in_city", JValue.obj(
      "city" -> JValue.str(city)
    )))

  def getCarTypesAvailable(companyNames: List[String]): Map[String, List[String]] =
    parseStringListMap(callToolRepr("get_car_types_available", JValue.obj(
      "company_name" -> JValue.arr(companyNames.map(JValue.str)*)
    )))

  def getRatingReviewsForCarRental(companyNames: List[String]): Classified[Map[String, RatedReviews]] =
    ClassifiedImpl.wrap:
      parseRatedReviewsMap(callToolRepr("get_rating_reviews_for_car_rental", JValue.obj(
        "company_name" -> JValue.arr(companyNames.map(JValue.str)*)
      )))

  def getCarFuelOptions(companyNames: List[String]): Map[String, List[String]] =
    parseStringListMap(callToolRepr("get_car_fuel_options", JValue.obj(
      "company_name" -> JValue.arr(companyNames.map(JValue.str)*)
    )))

  def getCarRentalAddress(companyNames: List[String]): Map[String, String] =
    parseStringMap(callToolRepr("get_car_rental_address", JValue.obj(
      "company_name" -> JValue.arr(companyNames.map(JValue.str)*)
    )))

  def getCarPricePerDay(companyNames: List[String]): Map[String, Double] =
    parseDoubleMap(callToolRepr("get_car_price_per_day", JValue.obj(
      "company_name" -> JValue.arr(companyNames.map(JValue.str)*)
    )))

  def createCalendarEvent(
      title: String,
      startTime: String,
      endTime: String,
      description: String = "",
      participants: Option[List[String]] = None,
      location: Option[String] = None
  ): CalendarEvent =
    val base = JValue.obj(
      "title" -> JValue.str(title),
      "start_time" -> JValue.str(startTime),
      "end_time" -> JValue.str(endTime),
      "description" -> JValue.str(description)
    )
    val opts = JValue.objOpt(
      "participants" -> participants.map(xs => JValue.arr(xs.map(JValue.str)*)),
      "location" -> location.map(JValue.str)
    )
    parseEvent(callToolJson("create_calendar_event", base.merge(opts)))

  def searchCalendarEvents(query: String, date: Option[String] = None): Classified[List[CalendarEvent]] =
    ClassifiedImpl.wrap:
      val args = JValue.obj("query" -> JValue.str(query)).merge(
        JValue.objOpt("date" -> date.map(JValue.str))
      )
      callToolJson("search_calendar_events", args)
        .asArray.getOrElse(Nil).map(parseEvent)

  def getDayCalendarEvents(day: String): Classified[List[CalendarEvent]] =
    ClassifiedImpl.wrap:
      callToolJson("get_day_calendar_events", JValue.obj("day" -> JValue.str(day)))
        .asArray.getOrElse(Nil).map(parseEvent)

  def cancelCalendarEvent(eventId: String): String =
    callToolText("cancel_calendar_event", JValue.obj("event_id" -> JValue.str(eventId)))

  def reserveHotel(hotel: String, startDay: String, endDay: String): String =
    callToolText("reserve_hotel", JValue.obj(
      "hotel" -> JValue.str(hotel),
      "start_day" -> JValue.str(startDay),
      "end_day" -> JValue.str(endDay)
    ))

  def reserveCarRental(company: String, startTime: String, endTime: Option[String]): String =
    callToolText("reserve_car_rental", JValue.obj(
      "company" -> JValue.str(company),
      "start_time" -> JValue.str(startTime),
      "end_time" -> endTime.map(JValue.str).getOrElse(JValue.nil)
    ))

  def reserveRestaurant(restaurant: String, startTime: String): String =
    callToolText("reserve_restaurant", JValue.obj(
      "restaurant" -> JValue.str(restaurant),
      "start_time" -> JValue.str(startTime)
    ))

  def getFlightInformation(departureCity: String, arrivalCity: String): List[FlightInformation] =
    parseFlights(
      callToolText("get_flight_information", JValue.obj(
        "departure_city" -> JValue.str(departureCity),
        "arrival_city" -> JValue.str(arrivalCity)
      )),
      departureCity,
      arrivalCity
    )

  def sendEmail(
      recipients: List[String],
      subject: String,
      body: String,
      attachments: Option[List[Attachment]] = None,
      cc: Option[List[String]] = None,
      bcc: Option[List[String]] = None
  ): Email =
    val base = JValue.obj(
      "recipients" -> JValue.arr(recipients.map(JValue.str)*),
      "subject" -> JValue.str(subject),
      "body" -> JValue.str(body)
    )
    val opts = JValue.objOpt(
      "attachments" -> attachments.map(xs => JValue.arr(xs.map(attachmentToJValue)*)),
      "cc" -> cc.map(xs => JValue.arr(xs.map(JValue.str)*)),
      "bcc" -> bcc.map(xs => JValue.arr(xs.map(JValue.str)*))
    )
    parseEmail(callToolJson("send_email", base.merge(opts)))

  private lazy val llmOps: LlmOps =
    def requireEnv(name: String): String =
      sys.env.getOrElse(name,
        throw RuntimeException(s"$name environment variable is not set"))
    LlmOps(Some(LlmConfig(
      baseUrl = "https://openrouter.ai/api/v1",
      apiKey = requireEnv("OPENROUTER_API_KEY"),
      model = "anthropic/claude-sonnet-4-6"
    )))

  def prompt(input: String): String =
    llmOps.chat(input)

  def displaySecurely(x: Classified[String]): Unit =
    ClassifiedImpl.unwrap(x).foreach: msg =>
      Files.writeString(
        Path.of(secureOutputPath).nn,
        msg + "\n",
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      )

  private def callToolText(name: String, arguments: JValue): String =
    val result = client.callTool(name, arguments)
    val text = result.field("content")(0).field("text").asString.getOrElse(
      throw MCPError(s"Failed to extract text from $name")
    )
    if text.startsWith("Error: ") then
      throw MCPError(s"$name: ${text.stripPrefix("Error: ")}")
    text

  private def callToolJson(name: String, arguments: JValue): JValue =
    JValue.parse(callToolText(name, arguments))

  private def callToolRepr(name: String, arguments: JValue): JValue =
    JValue.parse(TextParsers.pythonReprToJson(callToolText(name, arguments)))

  private def parseUserInformation(j: JValue): UserInformation =
    UserInformation(
      firstName = j.field("First Name").asString.getOrElse(""),
      lastName = j.field("Last Name").asString.getOrElse(""),
      idNumber = j.field("ID Number").asString.getOrElse(""),
      email = j.field("Email").asString.getOrElse(""),
      phoneNumber = j.field("Phone Number").asString.getOrElse(""),
      address = j.field("Address").asString.getOrElse(""),
      passportNumber = j.field("Passport Number").asString.getOrElse(""),
      bankAccountNumber = j.field("Bank Account Number").asString.getOrElse(""),
      creditCardNumber = j.field("Credit Card Number").asString.getOrElse("")
    )

  private def parseLabeledLines(text: String): List[String] =
    val body =
      val colon = text.indexOf(':')
      if colon >= 0 then text.substring(colon + 1) else text
    body.linesIterator.map(_.trim).filter(_.nonEmpty).toList

  private def parseStringMap(j: JValue): Map[String, String] =
    j.asObject.getOrElse(Nil).iterator.flatMap: (key, value) =>
      value.asString.map(key -> _)
    .toMap

  private def parseDoubleMap(j: JValue): Map[String, Double] =
    j.asObject.getOrElse(Nil).iterator.flatMap: (key, value) =>
      value.asDouble.map(key -> _)
    .toMap

  private def parseStringListMap(j: JValue): Map[String, List[String]] =
    j.asObject.getOrElse(Nil).iterator.map: (key, value) =>
      key -> value.asArray.getOrElse(Nil).flatMap(_.asString)
    .toMap

  private def parsePriceRangeMap(j: JValue): Map[String, PriceRange] =
    j.asObject.getOrElse(Nil).iterator.flatMap: (key, value) =>
      value.asString.map(s => key -> parsePriceRange(s))
    .toMap

  private def parseRatedReviewsMap(j: JValue): Map[String, RatedReviews] =
    j.asObject.getOrElse(Nil).iterator.flatMap: (key, value) =>
      value.asString.map(s => key -> parseRatedReviews(s))
    .toMap

  private def parsePriceRange(text: String): PriceRange =
    text match
      case TravelImpl.PriceRangePattern(min, max) =>
        PriceRange(min.nn.toDouble, max.nn.toDouble)
      case other =>
        throw MCPError(s"Invalid price range: $other")

  private def parseRatedReviews(text: String): RatedReviews =
    val lines = text.linesIterator.toList
    lines match
      case ratingLine :: reviewLines if ratingLine.startsWith("Rating: ") =>
        val rating = ratingLine.stripPrefix("Rating: ").trim.toDouble
        val reviews = reviewLines match
          case first :: rest =>
            val firstReview = first.stripPrefix("Reviews: ").trim
            (if firstReview.nonEmpty then List(firstReview) else Nil) ++
              rest.map(_.trim).filter(_.nonEmpty)
          case Nil => Nil
        RatedReviews(rating, reviews)
      case other =>
        throw MCPError(s"Invalid rating/reviews text: ${other.mkString("\\n")}")

  private def parseFlights(
      text: String,
      departureCity: String,
      arrivalCity: String
  ): List[FlightInformation] =
    text.linesIterator.map(_.trim).filter(_.nonEmpty).toList.map:
      case TravelImpl.FlightPattern(airline, flightNumber, departureTime, arrivalTime, price, contactInformation) =>
        FlightInformation(
          airline = airline.nn,
          flightNumber = flightNumber.nn,
          departureCity = departureCity,
          arrivalCity = arrivalCity,
          departureTime = departureTime.nn,
          arrivalTime = arrivalTime.nn,
          price = price.nn.toDouble,
          contactInformation = contactInformation.nn
        )
      case other =>
        throw MCPError(s"Invalid flight information: $other")

  private def parseEmail(j: JValue): Email =
    Email(
      id = j.field("id_").asString.getOrElse(""),
      sender = j.field("sender").asString.getOrElse(""),
      recipients = j.field("recipients").asArray.getOrElse(Nil).flatMap(_.asString),
      cc = j.field("cc").asArray.getOrElse(Nil).flatMap(_.asString),
      bcc = j.field("bcc").asArray.getOrElse(Nil).flatMap(_.asString),
      subject = j.field("subject").asString.getOrElse(""),
      body = j.field("body").asString.getOrElse(""),
      status = parseEmailStatus(j.field("status").asString.getOrElse("received")),
      read = j.field("read").asBool.getOrElse(false),
      timestamp = j.field("timestamp").asString.getOrElse(""),
      attachments = j.field("attachments").asArray.getOrElse(Nil).map(parseAttachment)
    )

  private def parseAttachment(j: JValue): Attachment =
    j.asString match
      case Some(fileId) => Attachment.FileRef(fileId)
      case None => Attachment.EventRef(parseEvent(j))

  private def parseEvent(j: JValue): CalendarEvent =
    CalendarEvent(
      id = j.field("id_").asString.getOrElse(""),
      title = j.field("title").asString.getOrElse(""),
      description = j.field("description").asString.getOrElse(""),
      startTime = j.field("start_time").asString.getOrElse(""),
      endTime = j.field("end_time").asString.getOrElse(""),
      location = j.field("location").asString,
      participants = j.field("participants").asArray.getOrElse(Nil).flatMap(_.asString),
      allDay = j.field("all_day").asBool.getOrElse(false),
      status = parseEventStatus(j.field("status").asString.getOrElse("confirmed"))
    )

  private def parseEmailStatus(s: String): EmailStatus = s match
    case "sent" => EmailStatus.Sent
    case "received" => EmailStatus.Received
    case "draft" => EmailStatus.Draft
    case other => throw MCPError(s"Unknown EmailStatus: $other")

  private def parseEventStatus(s: String): EventStatus = s match
    case "confirmed" => EventStatus.Confirmed
    case "canceled" => EventStatus.Canceled
    case other => throw MCPError(s"Unknown EventStatus: $other")

  private def attachmentToJValue(a: Attachment): JValue = a match
    case Attachment.FileRef(fileId) =>
      JValue.obj(
        "type" -> JValue.str("file"),
        "file_id" -> JValue.str(fileId)
      )
    case Attachment.EventRef(event) =>
      JValue.obj(
        "type" -> JValue.str("event"),
        "event_details" -> calendarEventToJValue(event)
      )

  private def calendarEventToJValue(e: CalendarEvent): JValue =
    JValue.obj(
      "id_" -> JValue.str(e.id),
      "title" -> JValue.str(e.title),
      "description" -> JValue.str(e.description),
      "start_time" -> JValue.str(e.startTime),
      "end_time" -> JValue.str(e.endTime),
      "location" -> e.location.map(JValue.str).getOrElse(JValue.nil),
      "participants" -> JValue.arr(e.participants.map(JValue.str)*),
      "all_day" -> JValue.bool(e.allDay),
      "status" -> JValue.str(eventStatusToWire(e.status))
    )

  private def eventStatusToWire(s: EventStatus): String = s match
    case EventStatus.Confirmed => "confirmed"
    case EventStatus.Canceled => "canceled"

@assumeSafe
object TravelImpl:
  private val PriceRangePattern: Regex =
    raw"Price range: ([0-9]+(?:\.[0-9]+)?) - ([0-9]+(?:\.[0-9]+)?)".r

  private val FlightPattern: Regex =
    raw"Airline: (.*?), Flight Number: (.*?), Departure Time: (.*?), Arrival Time: (.*?), Price: ([0-9]+(?:\.[0-9]+)?), Contact Information: (.*)".r
