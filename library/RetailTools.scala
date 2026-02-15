package library
package facade.retail

import mcpclient.*
import language.experimental.captureChecking

// ─── Global state ───────────────────────────────────────────────────────────

var _client: MCPClient | Null = null

/** Connect to a retail MCP server. Must be called before using any tool. */
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
def getUserDetails(userId: String): String =
  call("get_user_details", Json.obj(
    "user_id" -> Json.str(userId)
  ))

/** Get the status and details of an order. */
def getOrderDetails(orderId: String): String =
  call("get_order_details", Json.obj(
    "order_id" -> Json.str(orderId)
  ))

/** Get the inventory details of a product. */
def getProductDetails(productId: String): String =
  call("get_product_details", Json.obj(
    "product_id" -> Json.str(productId)
  ))

/** List the name and product id of all product types. */
def listAllProductTypes(): String =
  call("list_all_product_types")

/** Calculate the result of a mathematical expression. */
def calculate(expression: String): String =
  call("calculate", Json.obj(
    "expression" -> Json.str(expression)
  ))

// ─── Order modification tools (pending orders) ─────────────────────────────

/** Cancel a pending order. The refund goes to gift card balance immediately
 *  if paid by gift card, otherwise 5-7 business days. */
def cancelPendingOrder(orderId: String, reason: String): String =
  call("cancel_pending_order", Json.obj(
    "order_id" -> Json.str(orderId),
    "reason" -> Json.str(reason)
  ))

/** Modify the shipping address of a pending order. */
def modifyPendingOrderAddress(
  orderId: String,
  address1: String,
  address2: String,
  city: String,
  state: String,
  country: String,
  zip: String
): String =
  call("modify_pending_order_address", Json.obj(
    "order_id" -> Json.str(orderId),
    "address1" -> Json.str(address1),
    "address2" -> Json.str(address2),
    "city" -> Json.str(city),
    "state" -> Json.str(state),
    "country" -> Json.str(country),
    "zip" -> Json.str(zip)
  ))

/** Modify items in a pending order to new items of the same product type.
 *  Can only be called once per pending order. */
def modifyPendingOrderItems(
  orderId: String,
  itemIds: List[String],
  newItemIds: List[String],
  paymentMethodId: String
): String =
  call("modify_pending_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "new_item_ids" -> Json.arr(newItemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  ))

/** Modify the payment method of a pending order. */
def modifyPendingOrderPayment(orderId: String, paymentMethodId: String): String =
  call("modify_pending_order_payment", Json.obj(
    "order_id" -> Json.str(orderId),
    "payment_method_id" -> Json.str(paymentMethodId)
  ))

// ─── Delivered order tools ──────────────────────────────────────────────────

/** Return some items of a delivered order.
 *  The user will receive a follow-up email for how and where to return. */
def returnDeliveredOrderItems(
  orderId: String,
  itemIds: List[String],
  paymentMethodId: String
): String =
  call("return_delivered_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  ))

/** Exchange items in a delivered order to new items of the same product type.
 *  Can only be done once per delivered order. */
def exchangeDeliveredOrderItems(
  orderId: String,
  itemIds: List[String],
  newItemIds: List[String],
  paymentMethodId: String
): String =
  call("exchange_delivered_order_items", Json.obj(
    "order_id" -> Json.str(orderId),
    "item_ids" -> Json.arr(itemIds.map(Json.str)*),
    "new_item_ids" -> Json.arr(newItemIds.map(Json.str)*),
    "payment_method_id" -> Json.str(paymentMethodId)
  ))

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
): String =
  call("modify_user_address", Json.obj(
    "user_id" -> Json.str(userId),
    "address1" -> Json.str(address1),
    "address2" -> Json.str(address2),
    "city" -> Json.str(city),
    "state" -> Json.str(state),
    "country" -> Json.str(country),
    "zip" -> Json.str(zip)
  ))

// ─── Other tools ────────────────────────────────────────────────────────────

/** Transfer the user to a human agent with a summary of the issue. */
def transferToHumanAgents(summary: String): String =
  call("transfer_to_human_agents", Json.obj(
    "summary" -> Json.str(summary)
  ))
