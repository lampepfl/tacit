import functools
import json
import socket
import threading
from copy import deepcopy
from datetime import date, datetime
from typing import Any, Literal, Optional

import uvicorn
from loguru import logger
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field

from tau2.data_model.message import (
    AssistantMessage,
    Message,
    ToolCall,
    ToolMessage,
    UserMessage,
)
from tau2.data_model.tasks import EnvAssertion, EnvFunctionCall, InitializationData
from tau2.environment.db import DB
from tau2.environment.tool import Tool, as_tool
from tau2.environment.toolkit import ToolKitBase, ToolSignature, get_tool_signatures
from tau2.utils.mcp_client import create_safe_exec_client


FACADE_DESCRIPTIONS = {
    "airline": """
# Airline Tools API Reference

## Data Types

### Input types (for constructing arguments)

```scala
/** Flight number and date pair, used when booking or updating flights. */
case class FlightInfo(
  flightNumber: String,  // Flight number, such as "HAT001"
  date: String           // Date in "YYYY-MM-DD" format, such as "2024-05-01"
)

/** Passenger information. */
case class Passenger(
  firstName: String,  // Passenger's first name
  lastName: String,   // Passenger's last name
  dob: String         // Date of birth in "YYYY-MM-DD" format
)

/** Payment identifier and amount, used when booking. */
case class Payment(
  paymentId: String,  // Unique identifier for the payment
  amount: Int         // Payment amount in dollars
)

enum FlightType { case RoundTrip, OneWay }
enum Cabin { case Business, Economy, BasicEconomy }
enum Insurance { case Yes, No }
```

### Return types (returned by tools)

```scala
/** IATA airport code and city name. */
case class AirportCode(
  iata: String,  // IATA code
  city: String   // City name
)

/** A person's name. */
case class Name(
  firstName: String,  // The person's first name
  lastName: String    // The person's last name
)

/** A postal address. */
case class Address(
  address1: String,          // Primary address line
  address2: Option[String],  // Secondary address line (optional)
  city: String,              // City name
  country: String,           // Country name
  state: String,             // State or province name
  zip: String                // Postal code
)

/** A payment method saved in the user's profile. Discriminated by subtype. */
enum PaymentMethod:
  case CreditCard(id: String, brand: String, lastFour: String)
  case GiftCard(id: String, amount: Double)    // Gift card value amount
  case Certificate(id: String, amount: Double) // Certificate value amount

/** A flight in a reservation, with origin, destination, date, and price. */
case class ReservationFlight(
  flightNumber: String,  // Unique flight identifier
  origin: String,        // IATA code for origin airport
  destination: String,   // IATA code for destination airport
  date: String,          // Flight date in "YYYY-MM-DD" format
  price: Int             // Flight price in dollars
)

/** User details. */
case class User(
  userId: String,                                // Unique identifier for the user
  name: Name,                                    // User's full name
  address: Address,                              // User's address information
  email: String,                                 // User's email address
  dob: String,                                   // Date of birth in "YYYY-MM-DD" format
  paymentMethods: Map[String, PaymentMethod],    // User's saved payment methods
  savedPassengers: List[Passenger],              // User's saved passenger information
  membership: String,                            // Membership level: "gold", "silver", or "regular"
  reservations: List[String]                     // List of user's reservation IDs
)

/** Reservation details. */
case class Reservation(
  reservationId: String,             // Unique identifier for the reservation
  userId: String,                    // ID of the user who made the reservation
  origin: String,                    // IATA code for trip origin
  destination: String,               // IATA code for trip destination
  flightType: String,                // "round_trip" or "one_way"
  cabin: String,                     // "business", "economy", or "basic_economy"
  flights: List[ReservationFlight],  // List of flights in the reservation
  passengers: List[Passenger],       // List of passengers on the reservation
  paymentHistory: List[Payment],     // History of payments for this reservation
  createdAt: String,                 // Timestamp in "YYYY-MM-DDTHH:MM:SS" format
  totalBaggages: Int,                // Total number of bags in reservation
  nonfreeBaggages: Int,              // Number of paid bags in reservation
  insurance: String,                 // "yes" or "no"
  status: Option[String]             // None normally, Some("cancelled") if cancelled
)

/** An available direct flight returned by search. */
case class DirectFlight(
  flightNumber: String,               // Unique flight identifier
  origin: String,                     // IATA code for origin airport
  destination: String,                // IATA code for destination airport
  status: String,                     // "available"
  scheduledDepartureTimeEst: String,  // Scheduled departure time in EST, e.g. "06:00:00"
  scheduledArrivalTimeEst: String,    // Scheduled arrival time in EST, e.g. "07:00:00"
  date: Option[String],               // Flight date in "YYYY-MM-DD" format
  availableSeats: Map[String, Int],   // Available seats by cabin class
  prices: Map[String, Int]            // Prices by cabin class
)
```

## Tools

### `listAllAirports(): List[AirportCode]`
Returns a list of all available airports.

### `getUserDetails(userId: String): User`
Get the details of a user, including their reservations.
- `userId`: The user ID, such as `"sara_doe_496"`.
- Returns: The user details.
- Raises: ValueError if the user is not found.

### `getReservationDetails(reservationId: String): Reservation`
Get the details of a reservation.
- `reservationId`: The reservation ID, such as `"8JX2WO"`.
- Returns: The reservation details.
- Raises: ValueError if the reservation is not found.

### `getFlightStatus(flightNumber: String, date: String): String`
Get the status of a flight.
- `flightNumber`: The flight number.
- `date`: The date of the flight.
- Returns: The status of the flight.
- Raises: ValueError if the flight is not found.

### `searchDirectFlight(origin: String, destination: String, date: String): List[DirectFlight]`
Search for direct flights between two cities on a specific date.
- `origin`: The origin city airport in three letters, such as `"JFK"`.
- `destination`: The destination city airport in three letters, such as `"LAX"`.
- `date`: The date of the flight in the format `"YYYY-MM-DD"`, such as `"2024-01-01"`.
- Returns: The direct flights between the two cities on the specific date.

### `searchOnestopFlight(origin: String, destination: String, date: String): List[List[DirectFlight]]`
Search for one-stop flights between two cities on a specific date.
- `origin`: The origin city airport in three letters, such as `"JFK"`.
- `destination`: The destination city airport in three letters, such as `"LAX"`.
- `date`: The date of the flight in the format `"YYYY-MM-DD"`, such as `"2024-05-01"`.
- Returns: A list of pairs of DirectFlight objects.

### `calculate(expression: String): String`
Calculate the result of a mathematical expression.
- `expression`: The mathematical expression to calculate, such as `"2 + 2"`. The expression can contain numbers, operators (+, -, *, /), parentheses, and spaces.
- Returns: The result of the mathematical expression.
- Raises: ValueError if the expression is invalid.

### `bookReservation(userId: String, origin: String, destination: String, flightType: FlightType, cabin: Cabin, flights: List[FlightInfo], passengers: List[Passenger], paymentMethods: List[Payment], totalBaggages: Int, nonfreeBaggages: Int, insurance: Insurance): Reservation`
Book a reservation.
- `userId`: The ID of the user to book the reservation such as `"sara_doe_496"`.
- `origin`: The IATA code for the origin city such as `"SFO"`.
- `destination`: The IATA code for the destination city such as `"JFK"`.
- `flightType`: The type of flight such as `FlightType.OneWay` or `FlightType.RoundTrip`.
- `cabin`: The cabin class such as `Cabin.BasicEconomy`, `Cabin.Economy`, or `Cabin.Business`.
- `flights`: An array of objects containing details about each piece of flight.
- `passengers`: An array of objects containing details about each passenger.
- `paymentMethods`: An array of objects containing details about each payment method.
- `totalBaggages`: The total number of baggage items to book the reservation.
- `nonfreeBaggages`: The number of non-free baggage items to book the reservation.
- `insurance`: Whether the reservation has insurance.

### `cancelReservation(reservationId: String): Reservation`
Cancel the whole reservation.
- `reservationId`: The reservation ID, such as `"ZFA04Y"`.
- Returns: The updated reservation.
- Raises: ValueError if the reservation is not found.

### `updateReservationFlights(reservationId: String, cabin: Cabin, flights: List[FlightInfo], paymentId: String): Reservation`
Update the flight information of a reservation.
- `reservationId`: The reservation ID, such as `"ZFA04Y"`.
- `cabin`: The cabin class of the reservation.
- `flights`: An array of objects containing details about each piece of flight in the ENTIRE new reservation. Even if a flight segment is not changed, it should still be included in the array.
- `paymentId`: The payment id stored in user profile, such as `"credit_card_7815826"`, `"gift_card_7815826"`, `"certificate_7815826"`.
- Returns: The updated reservation.
- Raises: ValueError if the reservation is not found, if the user is not found, if the payment method is not found, if the certificate cannot be used to update reservation, or if the gift card balance is not enough.

### `updateReservationPassengers(reservationId: String, passengers: List[Passenger]): Reservation`
Update the passenger information of a reservation.
- `reservationId`: The reservation ID, such as `"ZFA04Y"`.
- `passengers`: An array of objects containing details about each passenger.
- Returns: The updated reservation.
- Raises: ValueError if the reservation is not found, or if the number of passengers does not match.

### `updateReservationBaggages(reservationId: String, totalBaggages: Int, nonfreeBaggages: Int, paymentId: String): Reservation`
Update the baggage information of a reservation.
- `reservationId`: The reservation ID, such as `"ZFA04Y"`.
- `totalBaggages`: The updated total number of baggage items included in the reservation.
- `nonfreeBaggages`: The updated number of non-free baggage items included in the reservation.
- `paymentId`: The payment id stored in user profile, such as `"credit_card_7815826"`, `"gift_card_7815826"`, `"certificate_7815826"`.
- Returns: The updated reservation.
- Raises: ValueError if the reservation is not found, if the user is not found, if the payment method is not found, if the certificate cannot be used to update reservation, or if the gift card balance is not enough.

### `sendCertificate(userId: String, amount: Int): String`
Send a certificate to a user. Be careful!
- `userId`: The ID of the user to book the reservation, such as `"sara_doe_496"`.
- `amount`: The amount of the certificate to send.
- Returns: A message indicating the certificate was sent.
- Raises: ValueError if the user is not found.

### `transferToHumanAgents(summary: String): String`
Transfer the user to a human agent, with a summary of the user's issue. Only transfer if the user explicitly asks for a human agent, or given the policy and the available tools, you cannot solve the user's issue.
- `summary`: A summary of the user's issue.
- Returns: A message indicating the user has been transferred to a human agent.
""",
    "retail": """
# Retail Tools API Reference

## Tools

### `findUserIdByEmail(email: String): String`
Find user id by email. If the user is not found, the function will return an error message.
- `email`: The email of the user, such as `"something@example.com"`.
- Returns: The user id if found, otherwise an error message.
- Raises: ValueError if the user is not found.

### `findUserIdByNameZip(firstName: String, lastName: String, zip: String): String`
Find user id by first name, last name, and zip code. If the user is not found, the function will return an error message. By default, find user id by email, and only call this function if the user is not found by email or cannot remember email.
- `firstName`: The first name of the customer, such as `"John"`.
- `lastName`: The last name of the customer, such as `"Doe"`.
- `zip`: The zip code of the customer, such as `"12345"`.
- Returns: The user id if found, otherwise an error message.
- Raises: ValueError if the user is not found.

### `getUserDetails(userId: String): String`
Get the details of a user, including their orders.
- `userId`: The user id, such as `"sara_doe_496"`.
- Returns: The user details.
- Raises: ValueError if the user is not found.

### `getOrderDetails(orderId: String): String`
Get the status and details of an order.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- Returns: The order details.
- Raises: ValueError if the order is not found.

### `getProductDetails(productId: String): String`
Get the inventory details of a product.
- `productId`: The product id, such as `"6086499569"`. Be careful the product id is different from the item id.
- Returns: The product details.
- Raises: ValueError if the product is not found.

### `listAllProductTypes(): String`
List the name and product id of all product types. Each product type has a variety of different items with unique item ids and options. There are only 50 product types in the store.
- Returns: A JSON string mapping product names to their product IDs, sorted alphabetically by name.

### `calculate(expression: String): String`
Calculate the result of a mathematical expression.
- `expression`: The mathematical expression to calculate, such as `"2 + 2"`. The expression can contain numbers, operators (+, -, *, /), parentheses, and spaces.
- Returns: The result of the mathematical expression.
- Raises: ValueError if the expression is invalid.

### `cancelPendingOrder(orderId: String, reason: String): String`
Cancel a pending order. If the order is already processed or delivered, it cannot be cancelled. The agent needs to explain the cancellation detail and ask for explicit user confirmation (yes/no) to proceed. If the user confirms, the order status will be changed to 'cancelled' and the payment will be refunded. The refund will be added to the user's gift card balance immediately if the payment was made using a gift card, otherwise the refund would take 5-7 business days to process. The function returns the order details after the cancellation.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `reason`: The reason for cancellation, which should be either `"no longer needed"` or `"ordered by mistake"`.
- Returns: The order details after the cancellation.

### `modifyPendingOrderAddress(orderId: String, address1: String, address2: String, city: String, state: String, country: String, zip: String): String`
Modify the shipping address of a pending order. The agent needs to explain the modification detail and ask for explicit user confirmation (yes/no) to proceed.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `address1`: The first line of the address, such as `"123 Main St"`.
- `address2`: The second line of the address, such as `"Apt 1"` or `""`.
- `city`: The city, such as `"San Francisco"`.
- `state`: The state, such as `"CA"`.
- `country`: The country, such as `"USA"`.
- `zip`: The zip code, such as `"12345"`.
- Returns: The order details after the modification.
- Raises: ValueError if the order is not pending.

### `modifyPendingOrderItems(orderId: String, itemIds: List[String], newItemIds: List[String], paymentMethodId: String): String`
Modify items in a pending order to new items of the same product type. For a pending order, this function can only be called once. The agent needs to explain the exchange detail and ask for explicit user confirmation (yes/no) to proceed.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `itemIds`: The item ids to be modified, each such as `"1008292230"`. There could be duplicate items in the list.
- `newItemIds`: The item ids to be modified for, each such as `"1008292230"`. There could be duplicate items in the list. Each new item id should match the item id in the same position and be of the same product.
- `paymentMethodId`: The payment method id to pay or receive refund for the item price difference, such as `"gift_card_0000000"` or `"credit_card_0000000"`. These can be looked up from the user or order details.
- Returns: The order details after the modification.
- Raises: ValueError if the order is not pending, if the items to be modified do not exist, if the new items do not exist or do not match the old items, or if the number of items to be modified does not match.

### `modifyPendingOrderPayment(orderId: String, paymentMethodId: String): String`
Modify the payment method of a pending order. The agent needs to explain the modification detail and ask for explicit user confirmation (yes/no) to proceed.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `paymentMethodId`: The payment method id to pay or receive refund for the item price difference, such as `"gift_card_0000000"` or `"credit_card_0000000"`. These can be looked up from the user or order details.
- Returns: The order details after the modification.
- Raises: ValueError if the order is not pending, if the payment method does not exist, if the payment history has more than one payment, or if the new payment method is the same as the current one.

### `returnDeliveredOrderItems(orderId: String, itemIds: List[String], paymentMethodId: String): String`
Return some items of a delivered order. The order status will be changed to 'return requested'. The agent needs to explain the return detail and ask for explicit user confirmation (yes/no) to proceed. The user will receive follow-up email for how and where to return the item.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `itemIds`: The item ids to be returned, each such as `"1008292230"`. There could be duplicate items in the list.
- `paymentMethodId`: The payment method id to pay or receive refund for the item price difference, such as `"gift_card_0000000"` or `"credit_card_0000000"`. These can be looked up from the user or order details.
- Returns: The order details after requesting the return.
- Raises: ValueError if the order is not delivered, if the payment method is not the original payment method or a gift card, or if the items to be returned do not exist.

### `exchangeDeliveredOrderItems(orderId: String, itemIds: List[String], newItemIds: List[String], paymentMethodId: String): String`
Exchange items in a delivered order to new items of the same product type. For a delivered order, return or exchange can be only done once by the agent. The agent needs to explain the exchange detail and ask for explicit user confirmation (yes/no) to proceed.
- `orderId`: The order id, such as `"#W0000000"`. Be careful there is a '#' symbol at the beginning of the order id.
- `itemIds`: The item ids to be exchanged, each such as `"1008292230"`. There could be duplicate items in the list.
- `newItemIds`: The item ids to be exchanged for, each such as `"1008292230"`. There could be duplicate items in the list. Each new item id should match the item id in the same position and be of the same product.
- `paymentMethodId`: The payment method id to pay or receive refund for the item price difference, such as `"gift_card_0000000"` or `"credit_card_0000000"`. These can be looked up from the user or order details.
- Returns: The order details after the exchange.
- Raises: ValueError if the order is not delivered, if the items to be exchanged do not exist, if the new items do not exist or do not match the old items, or if the number of items to be exchanged does not match.

### `modifyUserAddress(userId: String, address1: String, address2: String, city: String, state: String, country: String, zip: String): String`
Modify the default address of a user. The agent needs to explain the modification detail and ask for explicit user confirmation (yes/no) to proceed.
- `userId`: The user id, such as `"sara_doe_496"`.
- `address1`: The first line of the address, such as `"123 Main St"`.
- `address2`: The second line of the address, such as `"Apt 1"` or `""`.
- `city`: The city, such as `"San Francisco"`.
- `state`: The state, such as `"CA"`.
- `country`: The country, such as `"USA"`.
- `zip`: The zip code, such as `"12345"`.
- Returns: The user details after the modification.
- Raises: ValueError if the user is not found.

### `transferToHumanAgents(summary: String): String`
Transfer the user to a human agent, with a summary of the user's issue. Only transfer if the user explicitly asks for a human agent, or given the policy and the available tools, you cannot solve the user's issue.
- `summary`: A summary of the user's issue.
- Returns: A message indicating the user has been transferred to a human agent.
""",
}


class EnvironmentInfo(BaseModel):
    """
    Environment information.
    """

    domain_name: str = Field(description="The name of the domain.")
    policy: str = Field(description="The policy of the agent.")
    tool_defs: Optional[dict[str, ToolSignature]] = Field(
        description="The tool definitions of the environment.", default=None
    )


class Environment:
    """
    Environment
    """

    def __init__(
        self,
        domain_name: str,
        policy: str,
        tools: Optional[ToolKitBase] = None,
        user_tools: Optional[ToolKitBase] = None,
        solo_mode: bool = False,
        scala_mode: bool = False,
    ):
        """
        Environment
        Args:
            domain_name: The name of the domain.
            policy: The policy of the domain.
            tools: The tools available to the assistant in the domain.
            user_tools: The tools available to the user in the domain.
            solo_mode: The agent will have access to both user and assistant tools.
            scala_mode: If True, expose tools via MCP and provide a single Scala execution tool.
        """
        self.domain_name = domain_name
        self.policy = policy
        self.tools = tools
        self.user_tools = user_tools
        self.solo_mode = solo_mode
        self._scala_mode = scala_mode
        self._mcp_server = None
        self._uvicorn_server = None
        self._server_thread = None
        self._mcp_client = None
        if self.solo_mode:
            self.validate_solo_mode()
        self.sync_tools()

        if self._scala_mode:
            assert domain_name in FACADE_DESCRIPTIONS, (
                f"Scala mode is not supported for domain '{domain_name}'. "
                f"Supported domains: {list(FACADE_DESCRIPTIONS.keys())}"
            )
            self._setup_scala_mode(domain_name)

    def get_domain_name(self) -> str:
        """
        Get the name of the domain.
        """
        return self.domain_name

    def get_policy(self) -> str:
        """
        Get the policy of the domain.
        """
        return self.policy

    def get_tools(self) -> list[Tool]:
        """
        Get the tools of the domain.
        In scala_mode, returns only the run tool.
        """
        if self.tools is None:
            raise ValueError("Tools not available")
        if self._scala_mode:
            return [self._run_tool]
        return list(self.tools.get_tools().values())

    def get_user_tools(self) -> list[Tool]:
        """
        Get the tools of the domain.
        """
        if self.user_tools is None:
            raise ValueError("User tools not available")
        return list(self.user_tools.get_tools().values())

    def register_custom_tools(self, tools: list[Tool]):
        if self.tools is None:
            return
        for tool in tools:
            if not self.tools.has_tool(tool.name):
                self.tools.register_tool(tool)

    def _setup_scala_mode(self, facade: str):
        """Set up MCP server and SafeExecMCP client for Scala execution mode."""
        if self.tools is None:
            raise ValueError("Tools must be available for scala_mode")

        # Start MCP streamable HTTP server with all tools
        self._implied_tool_calls: list[ToolCall] = []
        self._mcp_server = FastMCP("tau2-agent-tools")
        for tool in list(self.tools.get_tools().values()):
            # Wrap tool function to record implied tool calls
            original_func = tool._func
            tool_name = tool.name
            def _to_plain(v):
                if isinstance(v, BaseModel):
                    return v.model_dump()
                if isinstance(v, list):
                    return [_to_plain(item) for item in v]
                if isinstance(v, dict):
                    return {k: _to_plain(val) for k, val in v.items()}
                return v

            def make_wrapper(fn, name):
                @functools.wraps(fn)
                def wrapper(*args, **kwargs):
                    plain_args = {k: _to_plain(v) for k, v in kwargs.items()}
                    self._implied_tool_calls.append(
                        ToolCall(name=name, arguments=plain_args)
                    )
                    return fn(*args, **kwargs)
                return wrapper
            self._mcp_server.add_tool(
                make_wrapper(original_func, tool_name),
                name=tool.name,
                description=tool._get_description(),
            )

        # Find a free port
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind(("127.0.0.1", 0))
            mcp_port = s.getsockname()[1]

        app = self._mcp_server.streamable_http_app()
        config = uvicorn.Config(app, host="127.0.0.1", port=mcp_port, log_level="warning")
        self._uvicorn_server = uvicorn.Server(config)
        self._server_thread = threading.Thread(target=self._uvicorn_server.run, daemon=True)
        self._server_thread.start()
        logger.info(f"MCP streamable HTTP server started on port {mcp_port}")

        #from IPython import embed; embed()

        # Start MCP STDIO client for SafeExecMCP
        self._mcp_client = create_safe_exec_client(facade, mcp_port)
        self._mcp_client.start()
        logger.info("MCP STDIO client connected to SafeExecMCP")

        # Create the run tool
        mcp_client = self._mcp_client

        def run(code: str) -> str:
            """Run Scala code and get the output. This is stateful: all executions are in the same REPL session.

            Args:
                code: The Scala code to execute.

            Returns:
                The output of the execution.
            """
            result = mcp_client.call_tool("run", {"code": code})
            content = result.get("content", [])
            parts = [item["text"] for item in content if item.get("type") == "text"]
            return "\n".join(parts) if parts else str(result)

        self._run_tool = as_tool(run)

        # Replace toolkit tools with just the run tool
        self.tools.register_tool(self._run_tool)

        # Store facade name for description lookup
        self._scala_facade = facade

    def get_scala_info(self) -> Optional[str]:
        """Return the facade description if in scala_mode, else None."""
        if not self._scala_mode:
            return None
        return FACADE_DESCRIPTIONS.get(self._scala_facade, "")

    def stop(self):
        """Clean up MCP resources."""
        if self._mcp_client is not None:
            self._mcp_client.stop()
            logger.info("MCP STDIO client stopped")
        if self._uvicorn_server is not None:
            self._uvicorn_server.should_exit = True
            if self._server_thread is not None:
                self._server_thread.join(timeout=5)
            logger.info("MCP streamable HTTP server stopped")

    def get_tools_description(
        self, env_type: Literal["user", "assistant"]
    ) -> Optional[str]:
        """
        Return a description of the user tools.
        """
        if env_type == "user":
            tool_kit = self.user_tools
        elif env_type == "assistant":
            tool_kit = self.tools
        else:
            raise ValueError(f"Invalid environment type: {env_type}")
        if tool_kit is None:
            return None
        tools = sorted(tool_kit.get_tools().values(), key=lambda x: x.name)
        return "\n\n".join(
            [f"{i + 1}. {t.name}\n{t.short_desc}" for i, t in enumerate(tools)]
        )

    def use_tool(self, tool_name: str, **kwargs) -> Any:
        """
        Use a tool available to the assistant of the domain.
        """
        if self.tools is None:
            raise ValueError("Tools not available")
        return self.tools.use_tool(tool_name=tool_name, **kwargs)

    def use_user_tool(self, tool_name: str, **kwargs) -> Any:
        """
        Use a tool available to the user of the domain.
        """
        if self.user_tools is None:
            raise ValueError("User tools not available")
        return self.user_tools.use_tool(tool_name=tool_name, **kwargs)

    def make_tool_call(
        self,
        tool_name: str,
        requestor: Literal["user", "assistant"] = "assistant",
        **kwargs,
    ) -> Any:
        """
        Make a tool call based on the requestor.
        Args:
            tool_name: The name of the tool to call.
            requestor: The requestor of the tool call.
            kwargs: The arguments to pass to the tool.
        Returns:
            The response of the tool call.

        Note: This does not call sync_tools.
        """
        if requestor == "user":
            if self.solo_mode:
                raise ValueError("User tool calls are not allowed in solo mode")
            return self.use_user_tool(tool_name=tool_name, **kwargs)
        elif requestor == "assistant":
            if self.solo_mode and self.user_tools is not None:
                if self.user_tools.has_tool(tool_name):
                    return self.use_user_tool(tool_name=tool_name, **kwargs)
            return self.use_tool(tool_name=tool_name, **kwargs)
        else:
            raise ValueError(f"Invalid requestor: {requestor}")

    def sync_tools(self):
        """
        Sync the user and assistant tools.
        Subclass should override this method if tools need to be synced.
        """
        pass

    def run_env_function_call(self, env_function_call: EnvFunctionCall) -> Any:
        """
        Runs any function available on agent environment or user environment.
        """
        env_type = env_function_call.env_type
        func_name = env_function_call.func_name
        if env_type == "user":
            tool_kit = self.user_tools
        elif env_type == "assistant":
            tool_kit = self.tools
        else:
            raise ValueError(f"Invalid environment type: {env_type}")
        func = getattr(tool_kit, func_name)
        if func is None:
            raise ValueError(f"Function {func_name} not found in {env_type} tools")
        res = func(**env_function_call.arguments)
        self.sync_tools()
        return res

    def run_env_assertion(
        self,
        assertion: EnvAssertion,
        raise_assertion_error: bool = True,
    ) -> bool:
        """
        Runs any assertion function on agent tools or user tools.
        """
        if not isinstance(assertion, EnvAssertion):
            raise ValueError(f"Assertion must be an EnvAssertion. Got {assertion}")
        res = self.run_env_function_call(assertion)
        if not isinstance(res, bool):
            raise ValueError(
                f"Function {assertion.func_name} returned {type(res)} instead of bool"
            )
        assert_pass = res == assertion.assert_value
        if raise_assertion_error:
            assert assert_pass, assertion.message or f"Assertion failed: {assertion}"
        return assert_pass

    def run_env_function_calls(self, env_function_calls: list[EnvFunctionCall]) -> None:
        """
        Run a list of environment function calls. If the function call is an assertion,
        an assertion check will be performed.
        """
        for env_function_call in env_function_calls:
            if isinstance(env_function_call, EnvAssertion):
                self.run_env_assertion(env_function_call, raise_assertion_error=True)
            else:
                self.run_env_function_call(env_function_call)

    def get_info(self, include_tool_info: bool = False) -> EnvironmentInfo:
        """
        Get environment information.
        """
        return EnvironmentInfo(
            domain_name=self.domain_name,
            policy=self.policy,
            tool_defs=(
                get_tool_signatures(self.tools)
                if self.tools is not None and include_tool_info
                else None
            ),
            user_tool_defs=(
                get_tool_signatures(self.user_tools)
                if self.user_tools is not None and include_tool_info
                else None
            ),
        )

    def check_db(self, reference: DB) -> bool:
        """
        Compare the agent database with the reference
        """
        return self.get_db_hash() == reference.get_hash()

    def check_user_db(self, reference: DB) -> bool:
        """
        Compare the user database with the reference
        """
        return self.get_user_db_hash() == reference.get_hash()

    def get_db_hash(self) -> Optional[str]:
        """
        Get a hash of the agent database
        Returns None if the database is not available
        """
        if self.tools is None:
            return None
        return self.tools.get_db_hash()

    def get_user_db_hash(self) -> Optional[str]:
        """
        Get a hash of the user database
        Returns None if the database is not available
        """
        if self.user_tools is None:
            return None
        return self.user_tools.get_db_hash()

    def set_state(
        self,
        initialization_data: Optional[InitializationData],
        initialization_actions: Optional[list[EnvFunctionCall]],
        message_history: list[Message],
    ):
        """
        Set the state of the environment given initialization data and a list of messages.
        """
        if self.solo_mode:
            assert all(
                [not isinstance(message, UserMessage) for message in message_history]
            ), "User messages are not allowed in solo mode"

        def get_actions_from_messages(
            messages: list[Message],
        ) -> list[tuple[ToolCall, ToolMessage]]:
            """
            Get the actions from the messages.
            """
            messages = deepcopy(messages)[::-1]
            actions = []
            while messages:
                message = messages.pop()
                if isinstance(message, ToolMessage):
                    raise ValueError(
                        "Tool message not expected. Tool messages should always follow a tool call."
                    )
                if (
                    isinstance(message, (AssistantMessage, UserMessage))
                    and message.is_tool_call()
                ):
                    tool_calls = message.tool_calls
                    for tc in tool_calls:
                        if len(messages) == 0:
                            raise ValueError("Tool message expected. Got None.")
                        tm = messages.pop()
                        if not isinstance(tm, ToolMessage):
                            raise ValueError(f"Tool message expected. Got {type(tm)}")
                        if tc.id != tm.id:
                            raise ValueError(
                                f"Tool call id mismatch. Got {tc.id} and {tm.id}"
                            )
                        actions.append((tc, tm))

            return actions

        if initialization_data is not None:
            if initialization_data.agent_data is not None:
                self.tools.update_db(initialization_data.agent_data)
            if initialization_data.user_data is not None:
                self.user_tools.update_db(initialization_data.user_data)

        if initialization_actions is not None:
            for action in initialization_actions:
                self.run_env_function_call(action)

        action_responses = get_actions_from_messages(message_history)
        for tool_call, expected_response in action_responses:
            response = self.get_response(tool_call)
            try:
                content = json.loads(response.content)
            except json.JSONDecodeError:
                content = response.content
            try:
                expected_content = json.loads(expected_response.content)
            except json.JSONDecodeError:
                expected_content = expected_response.content
            if not self._contents_equal(content, expected_content):
                raise ValueError(
                    f"Tool call:\n{tool_call}\n\nReturned:\n{response}\n\nExpected:\n{expected_response}"
                )
        self.sync_tools()

    @staticmethod
    def _contents_equal(content: Any, expected: Any) -> bool:
        """Compare contents, normalizing JVM-specific hashes that vary between runs."""
        if content == expected:
            return True
        if isinstance(content, str) and isinstance(expected, str):
            import re
            # Normalize JVM lambda/object hashes like 0x0000000301664ac0
            pattern = r'0x[0-9a-fA-F]+'
            return re.sub(pattern, '<hash>', content) == re.sub(pattern, '<hash>', expected)
        return False

    @classmethod
    def to_json_str(cls, resp: Any) -> str:
        """
        Convert a response to a JSON string.
        """

        def _process(resp: Any) -> str:
            if isinstance(resp, BaseModel):
                return resp.model_dump()
            elif isinstance(resp, str):
                return resp
            elif resp is None:
                return resp
            elif isinstance(resp, (int, float, bool)):
                return str(resp)
            elif isinstance(resp, list):
                return [_process(item) for item in resp]
            elif isinstance(resp, tuple):
                return tuple(_process(item) for item in resp)
            elif isinstance(resp, dict):
                return {k: _process(v) for k, v in resp.items()}
            elif isinstance(resp, (datetime, date)):
                # TODO: this did not fix the error: Object of type date is not JSON serializable
                return resp.isoformat()
            else:
                raise ValueError(f"Unsupported type: {type(resp)}")

        if not isinstance(resp, str):
            return json.dumps(_process(resp), default=str)  # FIXME: add default=str
        return resp

    def set_solo_mode(self, solo_mode: bool):
        """
        Set the solo mode of the environment.
        """
        self.solo_mode = solo_mode
        if solo_mode:
            self.validate_solo_mode()

    def validate_solo_mode(self) -> None:
        """
        Validate the tool call in solo mode.
        """
        assistant_tool_names = set(self.tools.get_tools().keys())
        user_tool_names = (
            set(self.user_tools.get_tools().keys())
            if self.user_tools is not None
            else set()
        )
        overlap = assistant_tool_names & user_tool_names
        if len(overlap) > 0:
            raise ValueError(f"Tool names overlap: {overlap}")

    def get_response(self, message: ToolCall) -> ToolMessage:
        """
        Get the response of the domain. This also calls sync_tools.
        Args:
            message: The message to get the response for.
        Returns:
            The response of the tool call.
        """
        error = False
        # Clear implied tool calls before execution
        if self._scala_mode:
            self._implied_tool_calls.clear()
        try:
            resp = self.make_tool_call(
                message.name, requestor=message.requestor, **message.arguments
            )
            self.sync_tools()
        except Exception as e:
            resp = f"Error: {e}"
            error = True
        logger.debug(f"Response: {resp}")
        resp = self.to_json_str(resp)
        tool_message = ToolMessage(
            id=message.id,
            content=resp,
            requestor=message.requestor,
            role="tool",
            error=error,
        )
        # Attach implied tool calls from scala execution
        if self._scala_mode and self._implied_tool_calls:
            tool_message.implied_tool_calls = deepcopy(self._implied_tool_calls)
        return tool_message
