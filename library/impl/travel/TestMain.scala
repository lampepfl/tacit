package tacit.library.travel

import language.experimental.captureChecking

import tacit.library.{Classified, ClassifiedImpl}
import tacit.library.mcp.MCPError

import java.nio.charset.StandardCharsets
import java.nio.file.Files

private var passed = 0
private var failed = 0

private def unwrap[T](c: Classified[T]): T = ClassifiedImpl.unwrap(c).get

private def assert(cond: Boolean, msg: String): Unit =
  if cond then passed += 1
  else
    failed += 1
    System.err.nn.println(s"  FAIL: $msg")

private def assertEquals[T](actual: T, expected: T, label: String): Unit =
  assert(actual == expected, s"$label: expected $expected, got $actual")

private def section(name: String): Unit =
  System.out.nn.println(s"\n--- $name ---")

private def expectError(label: String)(thunk: => Any): Unit =
  try
    val _ = thunk
    failed += 1
    System.err.nn.println(s"  FAIL: $label: expected MCPError, got success")
  catch
    case _: MCPError => passed += 1
    case e: Throwable =>
      failed += 1
      System.err.nn.println(s"  FAIL: $label: expected MCPError, got ${e.getClass.getSimpleName}: ${e.getMessage}")

@main def TestTravel(args: String*): Unit =
  val argList = args.toList
  val port = argList.sliding(2).collectFirst {
    case "--agentdojo-port" :: p :: Nil => p.toInt
  }.getOrElse(49593)
  val endpoint = s"http://localhost:$port/mcp"
  val out = System.out.nn
  out.println(s"=== Travel Service Test (endpoint: $endpoint) ===")

  val secureOutputFile = Files.createTempFile("travel-secure-", ".txt").nn
  val svc = TravelImpl(endpoint, secureOutputFile.toString)
  val testTag = System.currentTimeMillis.toString
  val uniqueTitle = s"tacit-travel-event-$testTag"
  val uniqueSubject = s"tacit-travel-email-$testTag"
  try
    // ── User information ────────────────────────────────────────────

    section("getUserInformation")
    val user = svc.getUserInformation()
    assertEquals(user.firstName, "Emma", "user firstName")
    assertEquals(user.lastName, "Johnson", "user lastName")
    assertEquals(user.idNumber, "123456789", "user idNumber")
    assertEquals(user.email, "emma.johnson@bluesparrowtech.com", "user email")
    assertEquals(user.phoneNumber, "327-420-4923", "user phone")
    assertEquals(user.address, "123 Main Street, Anytown, USA", "user address")
    assertEquals(user.passportNumber, "HGK137803", "user passportNumber")
    assertEquals(user.bankAccountNumber, "0789765432", "user bankAccountNumber")
    assertEquals(user.creditCardNumber, "4237-4252-7456-2574", "user creditCardNumber")

    // Repeated calls must be idempotent
    val user2 = svc.getUserInformation()
    assertEquals(user, user2, "getUserInformation idempotent")

    // ── Hotels ─────────────────────────────────────────────────────

    section("getAllHotelsInCity")
    val parisHotels = svc.getAllHotelsInCity("Paris")
    assertEquals(parisHotels.size, 4, "Paris hotel count")
    assert(parisHotels.contains("Le Marais Boutique"), "Paris hotels include Le Marais Boutique")
    assert(parisHotels.contains("Good Night"), "Paris hotels include Good Night")
    assert(parisHotels.contains("Luxury Palace"), "Paris hotels include Luxury Palace")
    assert(parisHotels.contains("Montmartre Suites"), "Paris hotels include Montmartre Suites")

    val londonHotels = svc.getAllHotelsInCity("London")
    assertEquals(londonHotels.size, 5, "London hotel count")
    assert(londonHotels.contains("Cozy Stay"), "London hotels include Cozy Stay")
    assert(londonHotels.contains("Mom's Home"), "London hotels include Mom's Home (apostrophe)")
    assert(londonHotels.contains("London Luxury"), "London hotels include London Luxury")
    assert(londonHotels.contains("Covent Garden Retreat"), "London hotels include Covent Garden Retreat")
    assert(londonHotels.contains("Riverside View"), "London hotels include Riverside View")

    val tokyoHotels = svc.getAllHotelsInCity("Tokyo")
    assertEquals(tokyoHotels.size, 1, "Tokyo hotel count")
    assert(tokyoHotels.contains("City Hub"), "Tokyo hotels include City Hub")

    assertEquals(svc.getAllHotelsInCity("Berlin").size, 0, "Berlin hotels empty")
    assertEquals(svc.getAllHotelsInCity("").size, 0, "empty city hotels empty")

    section("getHotelsPrices")
    val hotelPrices = svc.getHotelsPrices(
      List("Le Marais Boutique", "Good Night", "Luxury Palace", "Montmartre Suites")
    )
    assertEquals(hotelPrices.size, 4, "price map size")
    assertEquals(hotelPrices.get("Le Marais Boutique"), Some(PriceRange(120.0, 180.0)), "Le Marais Boutique price")
    assertEquals(hotelPrices.get("Good Night"), Some(PriceRange(240.0, 400.0)), "Good Night price")
    assertEquals(hotelPrices.get("Luxury Palace"), Some(PriceRange(500.0, 1000.0)), "Luxury Palace price")
    assertEquals(hotelPrices.get("Montmartre Suites"), Some(PriceRange(110.0, 200.0)), "Montmartre Suites price")

    // Apostrophe-laden hotel name (Python switches to double-quoted repr)
    val momsPrice = svc.getHotelsPrices(List("Mom's Home"))
    assertEquals(momsPrice.get("Mom's Home"), Some(PriceRange(80.0, 150.0)), "Mom's Home price")

    // Unknown hotel names return an empty map entry
    val mixedPrices = svc.getHotelsPrices(List("Le Marais Boutique", "No Such Hotel"))
    assertEquals(mixedPrices.size, 1, "unknown hotel dropped from price map")
    assertEquals(mixedPrices.get("Le Marais Boutique").map(_.min), Some(120.0), "known hotel still present")

    // Empty request returns empty map
    assertEquals(svc.getHotelsPrices(Nil).size, 0, "empty price request")

    section("getHotelsAddress")
    assertEquals(
      svc.getHotelsAddress("Le Marais Boutique"),
      Some("12 Rue des Archives, 75004 Paris, France"),
      "Le Marais Boutique address"
    )
    // Hotel with apostrophe in name — exercises double-quoted Python repr path
    assertEquals(
      svc.getHotelsAddress("Mom's Home"),
      Some("123 Oxford Street, London W1D 2HG, United Kingdom"),
      "Mom's Home address"
    )
    assertEquals(svc.getHotelsAddress("Definitely Not A Hotel"), None, "missing hotel address")
    assertEquals(svc.getHotelsAddress(""), None, "empty hotel name returns None")

    section("getRatingReviewsForHotels")
    val hotelReviews = unwrap(svc.getRatingReviewsForHotels(
      List("Le Marais Boutique", "Luxury Palace")
    ))
    assertEquals(hotelReviews.size, 2, "hotel reviews size")
    assertEquals(hotelReviews("Le Marais Boutique").rating, 4.2, "Le Marais Boutique rating")
    assert(
      hotelReviews("Le Marais Boutique").reviews.exists(_.contains("Charming boutique hotel")),
      "Le Marais Boutique: description review present"
    )
    assert(
      hotelReviews("Le Marais Boutique").reviews.exists(_.contains("Awesome hotel")),
      "Le Marais Boutique: injected review present"
    )
    assertEquals(hotelReviews("Luxury Palace").rating, 5.0, "Luxury Palace rating")
    assert(
      hotelReviews("Luxury Palace").reviews.exists(_.contains("Breakfast was delicious")),
      "Luxury Palace: injected review present"
    )
    assert(
      hotelReviews("Luxury Palace").reviews.size >= 4,
      "Luxury Palace: at least 4 reviews"
    )

    // City Hub: review contains "The hotel's cafe" (apostrophe) — Python emits
    // a double-quoted value inside a single-quoted dict key. This used to break
    // `TextParsers.pythonReprToJson`; the fix handles both quote styles.
    val cityHubReviews = unwrap(svc.getRatingReviewsForHotels(List("City Hub")))
    assertEquals(cityHubReviews("City Hub").rating, 4.3, "City Hub rating")
    assert(
      cityHubReviews("City Hub").reviews.exists(_.contains("Great location in the heart of Shinjuku")),
      "City Hub: stable review parsed"
    )
    assert(
      cityHubReviews("City Hub").reviews.exists(_.contains("hotel's cafe")),
      "City Hub: apostrophe review parsed"
    )

    // Mom's Home: apostrophe in KEY, reviews without apostrophes
    val momReviews = unwrap(svc.getRatingReviewsForHotels(List("Mom's Home")))
    assertEquals(momReviews("Mom's Home").rating, 4.5, "Mom's Home rating")
    assert(
      momReviews("Mom's Home").reviews.exists(_.contains("home away from home")),
      "Mom's Home: description review parsed"
    )

    // ── Restaurants ────────────────────────────────────────────────

    section("getAllRestaurantsInCity")
    val parisRestaurants = svc.getAllRestaurantsInCity("Paris")
    assertEquals(parisRestaurants.size, 10, "Paris restaurant count")
    assert(parisRestaurants.contains("Breizh Café"), "Paris restaurants include Breizh Café")
    assert(parisRestaurants.contains("Miznon"), "Paris restaurants include Miznon")
    assert(parisRestaurants.contains("Chez L'Ami Jean"), "Paris restaurants include Chez L'Ami Jean (apostrophe)")
    assert(parisRestaurants.contains("The yard"), "Paris restaurants include The yard")
    assert(parisRestaurants.contains("Le Baratin"), "Paris restaurants include Le Baratin")

    val londonRestaurants = svc.getAllRestaurantsInCity("London")
    assertEquals(londonRestaurants.size, 3, "London restaurant count")
    assert(londonRestaurants.contains("Azabu Ramen"), "London restaurants include Azabu Ramen")
    assert(londonRestaurants.contains("House of Sushi"), "London restaurants include House of Sushi")
    assert(londonRestaurants.contains("Home Kitchen"), "London restaurants include Home Kitchen")

    assertEquals(svc.getAllRestaurantsInCity("Tokyo").size, 0, "Tokyo restaurants empty")

    section("getCuisineTypeForRestaurants")
    val cuisines = svc.getCuisineTypeForRestaurants(
      List("Breizh Café", "Miznon", "New Asiaway", "New Israeli Restaurant")
    )
    assertEquals(cuisines.get("Breizh Café"), Some("French"), "Breizh Café cuisine")
    assertEquals(cuisines.get("Miznon"), Some("Israeli"), "Miznon cuisine")
    assertEquals(cuisines.get("New Asiaway"), Some("Chinese"), "New Asiaway cuisine")
    assertEquals(cuisines.get("New Israeli Restaurant"), Some("Israeli"), "New Israeli Restaurant cuisine")

    // Apostrophe in name
    val chezCuisine = svc.getCuisineTypeForRestaurants(List("Chez L'Ami Jean"))
    assertEquals(chezCuisine.get("Chez L'Ami Jean"), Some("French"), "Chez L'Ami Jean cuisine")

    section("getRestaurantsAddress")
    val restaurantAddresses = svc.getRestaurantsAddress(
      List("Breizh Café", "Miznon", "Chez L'Ami Jean", "Bistrot Paul Bert")
    )
    assertEquals(
      restaurantAddresses.get("Breizh Café"),
      Some("109 Rue Vieille du Temple, 75003 Paris, France"),
      "Breizh Café address"
    )
    assertEquals(
      restaurantAddresses.get("Miznon"),
      Some("22 Rue des Ecouffes, 75004 Paris, France"),
      "Miznon address"
    )
    assertEquals(
      restaurantAddresses.get("Chez L'Ami Jean"),
      Some("27 Rue Malar, 75007 Paris, France"),
      "Chez L'Ami Jean address (apostrophe)"
    )
    assertEquals(
      restaurantAddresses.get("Bistrot Paul Bert"),
      Some("18 Rue Paul Bert, 75011 Paris, France"),
      "Bistrot Paul Bert address"
    )

    section("getRatingReviewsForRestaurants")
    val restaurantReviews = unwrap(svc.getRatingReviewsForRestaurants(
      List("Breizh Café", "Miznon", "Chez L'Ami Jean")
    ))
    assertEquals(restaurantReviews("Breizh Café").rating, 3.9, "Breizh Café rating")
    assert(
      restaurantReviews("Breizh Café").reviews.exists(_.contains("crepes")),
      "Breizh Café description review"
    )
    assert(
      restaurantReviews("Breizh Café").reviews.exists(_.contains("great ambiance")),
      "Breizh Café injected review"
    )
    assertEquals(restaurantReviews("Miznon").rating, 4.3, "Miznon rating")
    assertEquals(restaurantReviews("Chez L'Ami Jean").rating, 4.4, "Chez L'Ami Jean rating (apostrophe)")
    assert(
      restaurantReviews("Chez L'Ami Jean").reviews.exists(_.contains("Michelin")),
      "Chez L'Ami Jean description review"
    )

    section("getDietaryRestrictionsForAllRestaurants")
    val dietary = svc.getDietaryRestrictionsForAllRestaurants(List("Breizh Café", "Miznon"))
    assertEquals(
      dietary.get("Breizh Café"),
      Some("Vegetarian available, Gluten-free available"),
      "Breizh Café dietary restrictions"
    )
    assertEquals(dietary.get("Miznon"), Some("Gluten-free available"), "Miznon dietary restrictions")
    // Apostrophe name
    val chezDietary = svc.getDietaryRestrictionsForAllRestaurants(List("Chez L'Ami Jean"))
    assertEquals(chezDietary.get("Chez L'Ami Jean"), Some("Vegan available"), "Chez L'Ami Jean dietary")

    section("getContactInformationForRestaurants")
    val contacts = svc.getContactInformationForRestaurants(List("Breizh Café", "Miznon"))
    assertEquals(contacts.get("Breizh Café"), Some("Phone: +33 1 42 72 13 77"), "Breizh Café contact")
    assertEquals(contacts.get("Miznon"), Some("Phone: +33 1 42 74 83 58"), "Miznon contact")
    val chezContact = svc.getContactInformationForRestaurants(List("Chez L'Ami Jean"))
    assertEquals(chezContact.get("Chez L'Ami Jean"), Some("Phone: +33 1 47 05 86 89"), "Chez L'Ami Jean contact")

    section("getPriceForRestaurants")
    val restaurantPrices = svc.getPriceForRestaurants(
      List("Breizh Café", "Miznon", "Chez L'Ami Jean", "New Israeli Restaurant")
    )
    assertEquals(restaurantPrices.get("Breizh Café"), Some(60.0), "Breizh Café price")
    assertEquals(restaurantPrices.get("Miznon"), Some(15.0), "Miznon price")
    assertEquals(restaurantPrices.get("Chez L'Ami Jean"), Some(24.0), "Chez L'Ami Jean price")
    assertEquals(restaurantPrices.get("New Israeli Restaurant"), Some(20.0), "New Israeli Restaurant price")

    section("checkRestaurantOpeningHours")
    val openingHours = svc.checkRestaurantOpeningHours(List("Breizh Café", "Miznon"))
    assert(openingHours.get("Breizh Café").exists(_.contains("9:00 AM - 11:00 PM")), "Breizh Café opening hours")
    assert(openingHours.get("Miznon").exists(_.contains("12:00 PM - 11:00 PM")), "Miznon opening hours")
    val chezHours = svc.checkRestaurantOpeningHours(List("Chez L'Ami Jean"))
    assert(
      chezHours.get("Chez L'Ami Jean").exists(_.contains("12:00 PM - 2:00 PM")),
      "Chez L'Ami Jean opening hours"
    )

    // ── Car rental ─────────────────────────────────────────────────

    section("getAllCarRentalCompaniesInCity")
    val londonCompanies = svc.getAllCarRentalCompaniesInCity("London")
    assertEquals(londonCompanies.size, 2, "London car rentals count")
    assert(londonCompanies.contains("Green Motion"), "London includes Green Motion")
    assert(londonCompanies.contains("New Car Rental"), "London includes New Car Rental")

    val laCompanies = svc.getAllCarRentalCompaniesInCity("Los Angeles")
    assertEquals(laCompanies.size, 3, "LA car rentals count")
    assert(laCompanies.contains("SunSet Rent-A-Car"), "LA includes SunSet Rent-A-Car")
    assert(laCompanies.contains("Speedy Rentals"), "LA includes Speedy Rentals")
    assert(laCompanies.contains("LAX Car Rental"), "LA includes LAX Car Rental")

    val parisCompanies = svc.getAllCarRentalCompaniesInCity("Paris")
    assertEquals(parisCompanies.size, 2, "Paris car rentals count")
    assert(parisCompanies.contains("Paris Rent-a-Car"), "Paris includes Paris Rent-a-Car")
    assert(parisCompanies.contains("Eiffel Tower Car Rental"), "Paris includes Eiffel Tower Car Rental")

    assertEquals(svc.getAllCarRentalCompaniesInCity("San Francisco").size, 0, "SF car rentals empty")

    section("getCarTypesAvailable")
    val carTypes = svc.getCarTypesAvailable(List("Paris Rent-a-Car", "Eiffel Tower Car Rental"))
    assert(carTypes.get("Paris Rent-a-Car").exists(_.contains("Convertible")), "Paris Rent-a-Car convertible")
    assert(carTypes.get("Paris Rent-a-Car").exists(_.contains("Sedan")), "Paris Rent-a-Car sedan")
    assert(carTypes.get("Paris Rent-a-Car").exists(_.contains("SUV")), "Paris Rent-a-Car SUV")
    assert(carTypes.get("Eiffel Tower Car Rental").exists(_.contains("Truck")), "Eiffel Tower Car Rental truck")
    assertEquals(carTypes.get("Paris Rent-a-Car").get.size, 3, "Paris Rent-a-Car type count")

    val greenMotionTypes = svc.getCarTypesAvailable(List("Green Motion"))
    assertEquals(greenMotionTypes.get("Green Motion").get.size, 2, "Green Motion type count")
    assert(greenMotionTypes.get("Green Motion").exists(_.contains("SUV")), "Green Motion SUV")

    section("getRatingReviewsForCarRental")
    val carReviews = unwrap(svc.getRatingReviewsForCarRental(
      List("Paris Rent-a-Car", "Eiffel Tower Car Rental")
    ))
    assertEquals(carReviews("Paris Rent-a-Car").rating, 4.5, "Paris Rent-a-Car rating")
    assert(
      carReviews("Paris Rent-a-Car").reviews.exists(_.contains("Louvre")),
      "Paris Rent-a-Car Louvre review"
    )
    assertEquals(carReviews("Eiffel Tower Car Rental").rating, 5.0, "Eiffel Tower Car Rental rating")
    assert(
      carReviews("Eiffel Tower Car Rental").reviews.exists(_.contains("Clean and reliable")),
      "Eiffel Tower Car Rental description review"
    )

    section("getCarFuelOptions")
    val fuelOptions = svc.getCarFuelOptions(List("Paris Rent-a-Car", "Eiffel Tower Car Rental"))
    assert(fuelOptions.get("Paris Rent-a-Car").exists(_.contains("Electric")), "Paris Rent-a-Car electric")
    assert(fuelOptions.get("Paris Rent-a-Car").exists(_.contains("Regular")), "Paris Rent-a-Car regular")
    assert(fuelOptions.get("Paris Rent-a-Car").exists(_.contains("Premium")), "Paris Rent-a-Car premium")
    assertEquals(fuelOptions.get("Paris Rent-a-Car").get.size, 3, "Paris Rent-a-Car fuel count")
    assert(fuelOptions.get("Eiffel Tower Car Rental").exists(_.contains("Premium")), "Eiffel Tower Car Rental premium")

    // Green Motion only has Electric
    val gmFuel = svc.getCarFuelOptions(List("Green Motion"))
    assertEquals(gmFuel.get("Green Motion"), Some(List("Electric")), "Green Motion fuel")

    section("getCarRentalAddress")
    val carAddresses = svc.getCarRentalAddress(List("Paris Rent-a-Car", "Eiffel Tower Car Rental"))
    assertEquals(
      carAddresses.get("Paris Rent-a-Car"),
      Some("23 Rue de Rivoli, 75001 Paris, France"),
      "Paris Rent-a-Car address"
    )
    assertEquals(
      carAddresses.get("Eiffel Tower Car Rental"),
      Some("5 Avenue Anatole France, 75007 Paris, France"),
      "Eiffel Tower Car Rental address"
    )

    section("getCarPricePerDay")
    val carPrices = svc.getCarPricePerDay(
      List("Paris Rent-a-Car", "Eiffel Tower Car Rental", "SunSet Rent-A-Car", "LAX Car Rental")
    )
    assertEquals(carPrices.get("Paris Rent-a-Car"), Some(45.0), "Paris Rent-a-Car price/day")
    assertEquals(carPrices.get("Eiffel Tower Car Rental"), Some(60.0), "Eiffel Tower Car Rental price/day")
    assertEquals(carPrices.get("SunSet Rent-A-Car"), Some(45.0), "SunSet (int) price/day")
    assertEquals(carPrices.get("LAX Car Rental"), Some(39.99), "LAX (float) price/day")

    // ── Calendar ───────────────────────────────────────────────────

    section("searchCalendarEvents")
    val teamHits = unwrap(svc.searchCalendarEvents("Team"))
    assert(teamHits.exists(_.title == "Team Sync"), "finds Team Sync")
    assertEquals(teamHits.head.description, "Weekly team meeting to discuss project updates.", "Team Sync description")
    assertEquals(teamHits.head.location, Some("Conference Room B"), "Team Sync location")
    assert(teamHits.head.participants.contains("emma.johnson@bluesparrowtech.com"), "Team Sync includes owner")
    assert(teamHits.head.participants.contains("michael.smith@bluesparrowtech.com"), "Team Sync has Michael")
    assertEquals(teamHits.head.status, EventStatus.Confirmed, "Team Sync is confirmed")
    assertEquals(teamHits.head.allDay, false, "Team Sync not all day")

    // search by description
    val descHits = unwrap(svc.searchCalendarEvents("Catch up"))
    assert(descHits.exists(_.title == "Lunch with Sarah"), "search by description finds Lunch")

    // search with date filter
    val dateHits = unwrap(svc.searchCalendarEvents("Team", Some("2024-05-15")))
    assert(dateHits.exists(_.title == "Team Sync"), "search with date finds Team Sync")
    expectError("searchCalendarEvents no match") {
      unwrap(svc.searchCalendarEvents(s"no-travel-match-$testTag"))
    }
    expectError("searchCalendarEvents wrong date") {
      unwrap(svc.searchCalendarEvents("Team", Some("2024-06-01")))
    }

    section("getDayCalendarEvents")
    val dayEvents = unwrap(svc.getDayCalendarEvents("2024-05-15"))
    assertEquals(dayEvents.size, 2, "2024-05-15 has 2 events")
    assert(dayEvents.exists(_.title == "Team Sync"), "Team Sync on 2024-05-15")
    assert(dayEvents.exists(_.title == "Lunch with Sarah"), "Lunch with Sarah on 2024-05-15")
    assertEquals(unwrap(svc.getDayCalendarEvents("2024-06-01")).size, 0, "empty day returns empty list")

    section("createCalendarEvent")
    val createdEvent = svc.createCalendarEvent(
      title = uniqueTitle,
      startTime = "2024-06-20 09:00",
      endTime = "2024-06-20 10:00",
      description = "travel facade integration test",
      participants = Some(List("alice@example.com")),
      location = Some("Gate 7")
    )
    assertEquals(createdEvent.title, uniqueTitle, "created event title")
    assertEquals(createdEvent.description, "travel facade integration test", "created event description")
    assertEquals(createdEvent.location, Some("Gate 7"), "created event location")
    assertEquals(createdEvent.status, EventStatus.Confirmed, "created event status")
    assertEquals(createdEvent.allDay, false, "created event not all day")
    assert(createdEvent.participants.contains("alice@example.com"), "created event has explicit participant")
    assert(
      createdEvent.participants.contains("emma.johnson@bluesparrowtech.com"),
      "created event auto-includes owner"
    )
    assert(createdEvent.startTime.startsWith("2024-06-20"), "created event start time")
    assert(createdEvent.endTime.startsWith("2024-06-20"), "created event end time")
    assert(createdEvent.id.nonEmpty, "created event has id")

    // Event with default description/no participants/no location
    val minimalEvent = svc.createCalendarEvent(
      title = s"minimal-$testTag",
      startTime = "2024-06-21 09:00",
      endTime = "2024-06-21 10:00"
    )
    assertEquals(minimalEvent.title, s"minimal-$testTag", "minimal event title")
    assertEquals(minimalEvent.description, "", "minimal event default description")
    assertEquals(minimalEvent.location, None, "minimal event no location")
    assert(
      minimalEvent.participants.contains("emma.johnson@bluesparrowtech.com"),
      "minimal event still includes owner"
    )

    // Verify the created event is searchable and appears on its day
    val afterCreate = unwrap(svc.searchCalendarEvents(uniqueTitle))
    assert(afterCreate.exists(_.id == createdEvent.id), "created event searchable by title")
    val dayAfterCreate = unwrap(svc.getDayCalendarEvents("2024-06-20"))
    assert(dayAfterCreate.exists(_.id == createdEvent.id), "created event appears on day 2024-06-20")

    section("cancelCalendarEvent")
    val cancelMessage = svc.cancelCalendarEvent(createdEvent.id)
    assert(cancelMessage.contains(createdEvent.id), "cancel message mentions id")
    assert(
      cancelMessage.toLowerCase.nn.contains("cancel"),
      s"cancel message mentions cancellation: $cancelMessage"
    )
    val afterCancel = unwrap(svc.searchCalendarEvents(uniqueTitle))
    val canceled = afterCancel.find(_.id == createdEvent.id)
    assert(canceled.isDefined, "canceled event still retrievable by search")
    canceled.foreach(e => assertEquals(e.status, EventStatus.Canceled, "canceled event status"))

    // ── Reservations ───────────────────────────────────────────────

    section("reserveHotel")
    val hotelReservation = svc.reserveHotel("Le Marais Boutique", "2026-07-01", "2026-07-05")
    assert(hotelReservation.contains("Le Marais Boutique"), "reserveHotel mentions hotel")
    assert(hotelReservation.contains("2026-07-01"), "reserveHotel mentions start day")
    assert(hotelReservation.contains("2026-07-05"), "reserveHotel mentions end day")
    assert(
      hotelReservation.toLowerCase.nn.contains("successfully"),
      s"reserveHotel success message: $hotelReservation"
    )

    // Apostrophe hotel name
    val momsReservation = svc.reserveHotel("Mom's Home", "2026-07-10", "2026-07-15")
    assert(momsReservation.contains("Mom's Home"), "reserveHotel apostrophe name")

    section("reserveCarRental")
    val carReservation = svc.reserveCarRental(
      "Paris Rent-a-Car",
      "2026-07-01 09:00",
      Some("2026-07-03 09:00")
    )
    assert(carReservation.contains("Paris Rent-a-Car"), "reserveCarRental mentions company")
    assert(carReservation.contains("2026-07-03 09:00"), "reserveCarRental mentions end time")

    // End time omitted: Python emits literal "None" into the message
    val carReservationNoEnd = svc.reserveCarRental("Paris Rent-a-Car", "2026-07-01 09:00", None)
    assert(carReservationNoEnd.contains("Paris Rent-a-Car"), "reserveCarRental (no end) mentions company")
    assert(
      carReservationNoEnd.contains("None"),
      s"reserveCarRental (no end) includes 'None': $carReservationNoEnd"
    )

    section("reserveRestaurant")
    val restaurantReservation = svc.reserveRestaurant("Breizh Café", "2026-07-01 19:30")
    assert(restaurantReservation.contains("Breizh Café"), "reserveRestaurant mentions restaurant")
    assert(restaurantReservation.contains("2026-07-01"), "reserveRestaurant mentions date")
    assert(
      restaurantReservation.contains("19:30"),
      s"reserveRestaurant mentions start time: $restaurantReservation"
    )
    // Python auto-computes end time as start+2h
    assert(
      restaurantReservation.contains("21:30"),
      s"reserveRestaurant mentions computed end time: $restaurantReservation"
    )

    // Apostrophe restaurant name
    val chezReservation = svc.reserveRestaurant("Chez L'Ami Jean", "2026-07-01 20:00")
    assert(chezReservation.contains("Chez L'Ami Jean"), "reserveRestaurant apostrophe name")

    // ── Flights ────────────────────────────────────────────────────

    section("getFlightInformation")
    val flights = svc.getFlightInformation("Paris", "London")
    assertEquals(flights.size, 3, "Paris -> London flights count")

    val ba = flights.find(_.flightNumber == "BA123")
    assert(ba.isDefined, "flights include BA123")
    ba.foreach { f =>
      assertEquals(f.airline, "British Airways", "BA123 airline")
      assertEquals(f.departureCity, "Paris", "BA123 departureCity")
      assertEquals(f.arrivalCity, "London", "BA123 arrivalCity")
      assertEquals(f.price, 200.0, "BA123 price")
      assertEquals(f.contactInformation, "Phone: +44 123456789", "BA123 contact")
      assert(f.departureTime.contains("2024-05-16"), "BA123 departure date")
      assert(f.arrivalTime.contains("2024-05-16"), "BA123 arrival date")
    }

    val af = flights.find(_.flightNumber == "AF456")
    assert(af.isDefined, "flights include AF456")
    af.foreach { f =>
      assertEquals(f.airline, "Air France", "AF456 airline")
      assertEquals(f.price, 180.0, "AF456 price")
    }

    val ej = flights.find(_.flightNumber == "EJ789")
    assert(ej.isDefined, "flights include EJ789")
    ej.foreach { f =>
      assertEquals(f.airline, "EasyJet", "EJ789 airline")
      assertEquals(f.price, 150.0, "EJ789 price")
    }

    // Unknown route returns empty
    assertEquals(svc.getFlightInformation("San Francisco", "New York").size, 0, "missing route empty")
    assertEquals(svc.getFlightInformation("London", "Paris").size, 0, "reverse route empty")

    // ── sendEmail ──────────────────────────────────────────────────

    section("sendEmail")
    val attachEvent = CalendarEvent(
      id = s"travel-attach-$testTag",
      title = s"Travel attachment $testTag",
      description = "inline calendar attachment",
      startTime = "2024-07-01T10:00:00",
      endTime = "2024-07-01T11:00:00",
      location = Some("Terminal A"),
      participants = List("alice@example.com"),
      allDay = false,
      status = EventStatus.Confirmed
    )
    val sentEmail = svc.sendEmail(
      recipients = List("test.recipient@example.com"),
      subject = uniqueSubject,
      body = "travel facade send_email test",
      attachments = Some(List(
        Attachment.FileRef(s"travel-file-$testTag"),
        Attachment.EventRef(attachEvent)
      )),
      cc = Some(List("travel.cc@example.com")),
      bcc = Some(List("travel.bcc@example.com"))
    )
    assertEquals(sentEmail.subject, uniqueSubject, "sendEmail subject")
    assertEquals(sentEmail.body, "travel facade send_email test", "sendEmail body")
    assertEquals(sentEmail.sender, "emma.johnson@bluesparrowtech.com", "sendEmail sender")
    assertEquals(sentEmail.status, EmailStatus.Sent, "sendEmail status")
    assertEquals(sentEmail.read, true, "sent email marked read")
    assert(sentEmail.recipients.contains("test.recipient@example.com"), "sendEmail recipient")
    assertEquals(sentEmail.recipients.size, 1, "sendEmail recipient count")
    assert(sentEmail.cc.contains("travel.cc@example.com"), "sendEmail cc")
    assertEquals(sentEmail.cc.size, 1, "sendEmail cc count")
    assert(sentEmail.bcc.contains("travel.bcc@example.com"), "sendEmail bcc")
    assertEquals(sentEmail.bcc.size, 1, "sendEmail bcc count")
    assertEquals(sentEmail.attachments.size, 2, "sendEmail attachment count")
    assert(
      sentEmail.attachments.exists {
        case Attachment.FileRef(id) => id == s"travel-file-$testTag"
        case _ => false
      },
      "sendEmail file attachment round-trip"
    )
    assert(
      sentEmail.attachments.exists {
        case Attachment.EventRef(event) =>
          event.title == attachEvent.title && event.location == Some("Terminal A")
        case _ => false
      },
      "sendEmail event attachment round-trip"
    )
    assert(sentEmail.id.nonEmpty, "sendEmail returns id")
    assert(sentEmail.timestamp.nonEmpty, "sendEmail returns timestamp")

    // Minimal sendEmail (no cc/bcc/attachments)
    val minimalEmail = svc.sendEmail(
      recipients = List("plain@example.com"),
      subject = s"minimal-$testTag",
      body = "plain"
    )
    assertEquals(minimalEmail.subject, s"minimal-$testTag", "minimal email subject")
    assertEquals(minimalEmail.recipients, List("plain@example.com"), "minimal email recipients")
    assertEquals(minimalEmail.cc, Nil, "minimal email cc empty")
    assertEquals(minimalEmail.bcc, Nil, "minimal email bcc empty")
    assertEquals(minimalEmail.attachments, Nil, "minimal email attachments empty")

    // Multiple recipients
    val multiEmail = svc.sendEmail(
      recipients = List("a@example.com", "b@example.com", "c@example.com"),
      subject = s"multi-$testTag",
      body = "hi all"
    )
    assertEquals(multiEmail.recipients.size, 3, "multi email recipients")

    // ── displaySecurely ───────────────────────────────────────────

    section("displaySecurely")
    val secureReview = svc.getRatingReviewsForHotels(List("Le Marais Boutique"))
      .map(_.get("Le Marais Boutique").map(_.reviews.head).getOrElse(""))
    val secureEventTitle = svc.getDayCalendarEvents("2024-05-15")
      .map(_.headOption.map(_.title).getOrElse(""))
    val secureApostrophe = svc.getRatingReviewsForHotels(List("City Hub"))
      .map { m =>
        m.get("City Hub")
          .flatMap(_.reviews.find(_.contains("cafe")))
          .getOrElse("no match")
      }
    svc.displaySecurely(secureReview)
    svc.displaySecurely(secureEventTitle)
    svc.displaySecurely(secureApostrophe)
    val secureContent = Files.readString(secureOutputFile, StandardCharsets.UTF_8).nn
    assert(secureContent.contains("Charming boutique hotel"), "secure output contains hotel review")
    assert(secureContent.contains("Team Sync"), "secure output contains calendar title")
    assert(secureContent.contains("hotel's cafe"), "secure output contains apostrophe review")
    assertEquals(secureContent.count(_ == '\n'), 3, "secure output line count")

    // ── Classified safety: toString never reveals data ─────────────

    section("Classified safety")
    val classifiedReviews: Classified[Map[String, RatedReviews]] =
      svc.getRatingReviewsForHotels(List("Le Marais Boutique"))
    assertEquals(classifiedReviews.toString, "Classified(***)", "Classified toString redacted")

    if false then
      val _ : String = svc.prompt("Summarize Paris in one word.")

    out.println()
    out.println(s"=== $passed passed, $failed failed ===")
    if failed > 0 then System.exit(1)
  catch
    case e: MCPError =>
      System.err.nn.println(s"\nMCP error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
    case _: java.net.ConnectException =>
      System.err.nn.println(s"\nConnection refused: is the travel MCP server running at $endpoint?")
      System.exit(1)
    case e: Exception =>
      System.err.nn.println(s"\nUnexpected error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
  finally
    svc.close()
    Files.deleteIfExists(secureOutputFile)
