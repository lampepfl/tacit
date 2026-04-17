package tacit.library.travel

import language.experimental.captureChecking
import caps.*

import tacit.library.Classified

type Attachment = tacit.library.workspace.Attachment
val Attachment: tacit.library.workspace.Attachment.type = tacit.library.workspace.Attachment

type CalendarEvent = tacit.library.workspace.CalendarEvent
val CalendarEvent: tacit.library.workspace.CalendarEvent.type = tacit.library.workspace.CalendarEvent

type Email = tacit.library.workspace.Email
val Email: tacit.library.workspace.Email.type = tacit.library.workspace.Email

type EmailStatus = tacit.library.workspace.EmailStatus
val EmailStatus: tacit.library.workspace.EmailStatus.type = tacit.library.workspace.EmailStatus

type EventStatus = tacit.library.workspace.EventStatus
val EventStatus: tacit.library.workspace.EventStatus.type = tacit.library.workspace.EventStatus

@assumeSafe
case class UserInformation(
    firstName: String,
    lastName: String,
    idNumber: String,
    email: String,
    phoneNumber: String,
    address: String,
    passportNumber: String,
    bankAccountNumber: String,
    creditCardNumber: String
)

@assumeSafe
case class PriceRange(min: Double, max: Double)

@assumeSafe
case class RatedReviews(rating: Double, reviews: List[String])

@assumeSafe
case class FlightInformation(
    airline: String,
    flightNumber: String,
    departureCity: String,
    arrivalCity: String,
    departureTime: String,
    arrivalTime: String,
    price: Double,
    contactInformation: String
)

@assumeSafe
trait TravelService:
  def getUserInformation(): UserInformation

  def getAllHotelsInCity(city: String): List[String]
  def getHotelsPrices(hotelNames: List[String]): Map[String, PriceRange]
  def getRatingReviewsForHotels(hotelNames: List[String]): Classified[Map[String, RatedReviews]]
  def getHotelsAddress(hotelName: String): Option[String]

  def getAllRestaurantsInCity(city: String): List[String]
  def getCuisineTypeForRestaurants(restaurantNames: List[String]): Map[String, String]
  def getRestaurantsAddress(restaurantNames: List[String]): Map[String, String]
  def getRatingReviewsForRestaurants(restaurantNames: List[String]): Classified[Map[String, RatedReviews]]
  def getDietaryRestrictionsForAllRestaurants(restaurantNames: List[String]): Map[String, String]
  def getContactInformationForRestaurants(restaurantNames: List[String]): Map[String, String]
  def getPriceForRestaurants(restaurantNames: List[String]): Map[String, Double]
  def checkRestaurantOpeningHours(restaurantNames: List[String]): Map[String, String]

  def getAllCarRentalCompaniesInCity(city: String): List[String]
  def getCarTypesAvailable(companyNames: List[String]): Map[String, List[String]]
  def getRatingReviewsForCarRental(companyNames: List[String]): Classified[Map[String, RatedReviews]]
  def getCarFuelOptions(companyNames: List[String]): Map[String, List[String]]
  def getCarRentalAddress(companyNames: List[String]): Map[String, String]
  def getCarPricePerDay(companyNames: List[String]): Map[String, Double]

  def createCalendarEvent(
      title: String,
      startTime: String,
      endTime: String,
      description: String = "",
      participants: Option[List[String]] = None,
      location: Option[String] = None
  ): CalendarEvent
  def searchCalendarEvents(query: String, date: Option[String] = None): Classified[List[CalendarEvent]]
  def getDayCalendarEvents(day: String): Classified[List[CalendarEvent]]
  def cancelCalendarEvent(eventId: String): String

  def reserveHotel(hotel: String, startDay: String, endDay: String): String
  def reserveCarRental(company: String, startTime: String, endTime: Option[String]): String
  def reserveRestaurant(restaurant: String, startTime: String): String

  def getFlightInformation(departureCity: String, arrivalCity: String): List[FlightInformation]

  def sendEmail(
      recipients: List[String],
      subject: String,
      body: String,
      attachments: Option[List[Attachment]] = None,
      cc: Option[List[String]] = None,
      bcc: Option[List[String]] = None
  ): Email

  def prompt(input: String): String
  def displaySecurely(x: Classified[String]): Unit
