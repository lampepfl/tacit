package library
package facade.retail

import mcpclient.*
import language.experimental.captureChecking

// ─── Data types ─────────────────────────────────────────────────────────────

case class UserName(firstName: String, lastName: String)
object UserName:
  def fromJson(j: Json): UserName = UserName(
    firstName = j("first_name").asString.getOrElse(""),
    lastName = j("last_name").asString.getOrElse("")
  )

case class UserAddress(
  address1: String,
  address2: String,
  city: String,
  country: String,
  state: String,
  zip: String
)
object UserAddress:
  def fromJson(j: Json): UserAddress = UserAddress(
    address1 = j("address1").asString.getOrElse(""),
    address2 = j("address2").asString.getOrElse(""),
    city = j("city").asString.getOrElse(""),
    country = j("country").asString.getOrElse(""),
    state = j("state").asString.getOrElse(""),
    zip = j("zip").asString.getOrElse("")
  )

enum PaymentMethod:
  case CreditCard(id: String, brand: String, lastFour: String)
  case GiftCard(id: String, balance: Double)
  case Paypal(id: String)

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
        balance = j("balance").asDouble.getOrElse(0.0)
      )
      case "paypal" => Paypal(
        id = j("id").asString.getOrElse("")
      )
      case other => throw IllegalArgumentException(s"Unknown payment source: $other")

case class User(
  userId: String,
  name: UserName,
  address: UserAddress,
  email: String,
  paymentMethods: Map[String, PaymentMethod],
  orders: List[String]
)
object User:
  def fromJson(j: Json): User = User(
    userId = j("user_id").asString.getOrElse(""),
    name = UserName.fromJson(j("name")),
    address = UserAddress.fromJson(j("address")),
    email = j("email").asString.getOrElse(""),
    paymentMethods = decodeMap(j("payment_methods"), PaymentMethod.fromJson),
    orders = j("orders").asArray.getOrElse(Nil).flatMap(_.asString)
  )

case class Variant(
  itemId: String,
  options: Map[String, String],
  available: Boolean,
  price: Double
)
object Variant:
  def fromJson(j: Json): Variant = Variant(
    itemId = j("item_id").asString.getOrElse(""),
    options = decodeMap(j("options"), _.asString.getOrElse("")),
    available = j("available").asBool.getOrElse(false),
    price = j("price").asDouble.getOrElse(0.0)
  )

case class Product(
  name: String,
  productId: String,
  variants: Map[String, Variant]
)
object Product:
  def fromJson(j: Json): Product = Product(
    name = j("name").asString.getOrElse(""),
    productId = j("product_id").asString.getOrElse(""),
    variants = decodeMap(j("variants"), Variant.fromJson)
  )

case class OrderItem(
  name: String,
  productId: String,
  itemId: String,
  price: Double,
  options: Map[String, String]
)
object OrderItem:
  def fromJson(j: Json): OrderItem = OrderItem(
    name = j("name").asString.getOrElse(""),
    productId = j("product_id").asString.getOrElse(""),
    itemId = j("item_id").asString.getOrElse(""),
    price = j("price").asDouble.getOrElse(0.0),
    options = decodeMap(j("options"), _.asString.getOrElse(""))
  )

case class OrderFulfillment(
  trackingId: List[String],
  itemIds: List[String]
)
object OrderFulfillment:
  def fromJson(j: Json): OrderFulfillment = OrderFulfillment(
    trackingId = j("tracking_id").asArray.getOrElse(Nil).flatMap(_.asString),
    itemIds = j("item_ids").asArray.getOrElse(Nil).flatMap(_.asString)
  )

case class OrderPayment(
  transactionType: String,
  amount: Double,
  paymentMethodId: String
)
object OrderPayment:
  def fromJson(j: Json): OrderPayment = OrderPayment(
    transactionType = j("transaction_type").asString.getOrElse(""),
    amount = j("amount").asDouble.getOrElse(0.0),
    paymentMethodId = j("payment_method_id").asString.getOrElse("")
  )

case class Order(
  orderId: String,
  userId: String,
  address: UserAddress,
  items: List[OrderItem],
  status: String,
  fulfillments: List[OrderFulfillment],
  paymentHistory: List[OrderPayment],
  cancelReason: Option[String],
  exchangeItems: Option[List[String]],
  exchangeNewItems: Option[List[String]],
  exchangePaymentMethodId: Option[String],
  exchangePriceDifference: Option[Double],
  returnItems: Option[List[String]],
  returnPaymentMethodId: Option[String]
)
object Order:
  def fromJson(j: Json): Order = Order(
    orderId = j("order_id").asString.getOrElse(""),
    userId = j("user_id").asString.getOrElse(""),
    address = UserAddress.fromJson(j("address")),
    items = j("items").asArray.getOrElse(Nil).map(OrderItem.fromJson),
    status = j("status").asString.getOrElse(""),
    fulfillments = j("fulfillments").asArray.getOrElse(Nil).map(OrderFulfillment.fromJson),
    paymentHistory = j("payment_history").asArray.getOrElse(Nil).map(OrderPayment.fromJson),
    cancelReason = j("cancel_reason").asString,
    exchangeItems = j("exchange_items").asArray.map(_.flatMap(_.asString)),
    exchangeNewItems = j("exchange_new_items").asArray.map(_.flatMap(_.asString)),
    exchangePaymentMethodId = j("exchange_payment_method_id").asString,
    exchangePriceDifference = j("exchange_price_difference").asDouble,
    returnItems = j("return_items").asArray.map(_.flatMap(_.asString)),
    returnPaymentMethodId = j("return_payment_method_id").asString
  )

private def decodeMap[V](j: Json, f: Json => V): Map[String, V] =
  j.asObject match
    case Some(fields) => fields.map((k, v) => k -> f(v)).toMap
    case None => Map.empty

// ─── Global state ───────────────────────────────────────────────────────────

var _client: MCPClient | Null = null

/** Connect to a retail MCP server. Must be called before using any tool. */
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

// ─── User lookup tools ──────────────────────────────────────────────────────

/** Find user id by email. */
def findUserIdByEmail(email: String): String =
  call("find_user_id_by_email", Json.obj(
    "email" -> Json.str(email)
  ))

/** Find user id by first name, last name, and zip code.
 *  Use this only if the user cannot be found by email. */
def findUserIdByNameZip(firstName: String, lastName: String, zip: String): String =
  call("find_user_id_by_name_zip", Json.obj(
    "first_name" -> Json.str(firstName),
    "last_name" -> Json.str(lastName),
    "zip" -> Json.str(zip)
  ))

// ─── Query tools ────────────────────────────────────────────────────────────

/** Get the details of a user, including their orders. */
def getUserDetails(userId: String): User =
  User.fromJson(callJson("get_user_details", Json.obj(
    "user_id" -> Json.str(userId)
  )))

/** Get the status and details of an order. */
def getOrderDetails(orderId: String): Order =
  Order.fromJson(callJson("get_order_details", Json.obj(
    "order_id" -> Json.str(orderId)
  )))

/** Get the inventory details of a product. */
def getProductDetails(productId: String): Product =
  Product.fromJson(callJson("get_product_details", Json.obj(
    "product_id" -> Json.str(productId)
  )))

/** List the name and product id of all product types. */
def listAllProductTypes(): Map[String, String] =
  decodeMap(callJson("list_all_product_types"), _.asString.getOrElse(""))

/** Calculate the result of a mathematical expression. */
def calculate(expression: String): String =
  call("calculate", Json.obj(
    "expression" -> Json.str(expression)
  ))

// ─── Order modification tools (pending orders) ─────────────────────────────

/** Cancel a pending order. The refund goes to gift card balance immediately
 *  if paid by gift card, otherwise 5-7 business days. */
def cancelPendingOrder(orderId: String, reason: String): Order =
  Order.fromJson(callJson("cancel_pending_order", Json.obj(
    "order_id" -> Json.str(orderId),
    "reason" -> Json.str(reason)
  )))

/** Modify the shipping address of a pending order. */
def modifyPendingOrderAddress(
  orderId: String,
  address1: String,
  address2: String,
  city: String,
  state: String,
  country: String,
  zip: String
): Order =
  Order.fromJson(callJson("modify_pending_order_address", Json.obj(
    "order_id" -> Json.str(orderId),
    "address1" -> Json.str(address1),
    "address2" -> Json.str(address2),
    "city" -> Json.str(city),
    "state" -> Json.str(state),
    "country" -> Json.str(country),
    "zip" -> Json.str(zip)
  )))

/** Modify items in a pending order to new items of the same product type.
 *  Can only be called once per pending order. */
def modifyPendingOrderItems(
  orderId: String,
  itemIds: List[String],
  newItemIds: List[String],
  paymentMethodId: String
): Order =
  Order.fromJson(callJson("modify_pending_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "new_item_ids" -> Json.arr(newItemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  )))

/** Modify the payment method of a pending order. */
def modifyPendingOrderPayment(orderId: String, paymentMethodId: String): Order =
  Order.fromJson(callJson("modify_pending_order_payment", Json.obj(
    "order_id" -> Json.str(orderId),
    "payment_method_id" -> Json.str(paymentMethodId)
  )))

// ─── Delivered order tools ──────────────────────────────────────────────────

/** Return some items of a delivered order.
 *  The user will receive a follow-up email for how and where to return. */
def returnDeliveredOrderItems(
  orderId: String,
  itemIds: List[String],
  paymentMethodId: String
): Order =
  Order.fromJson(callJson("return_delivered_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  )))

/** Exchange items in a delivered order to new items of the same product type.
 *  Can only be done once per delivered order. */
def exchangeDeliveredOrderItems(
  orderId: String,
  itemIds: List[String],
  newItemIds: List[String],
  paymentMethodId: String
): Order =
  Order.fromJson(callJson("exchange_delivered_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "new_item_ids" -> Json.arr(newItemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  )))

// ─── User modification tools ────────────────────────────────────────────────

/** Modify the default address of a user. */
def modifyUserAddress(
  userId: String,
  address1: String,
  address2: String,
  city: String,
  state: String,
  country: String,
  zip: String
): User =
  User.fromJson(callJson("modify_user_address", Json.obj(
    "user_id" -> Json.str(userId),
    "address1" -> Json.str(address1),
    "address2" -> Json.str(address2),
    "city" -> Json.str(city),
    "state" -> Json.str(state),
    "country" -> Json.str(country),
    "zip" -> Json.str(zip)
  )))

// ─── Other tools ────────────────────────────────────────────────────────────

/** Transfer the user to a human agent with a summary of the issue. */
def transferToHumanAgents(summary: String): String =
  call("transfer_to_human_agents", Json.obj(
    "summary" -> Json.str(summary)
  ))
