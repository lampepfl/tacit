package tacit.library

import language.experimental.captureChecking

import com.sun.net.httpserver.{HttpServer, HttpExchange}

import java.net.InetSocketAddress

class WebOpsSuite extends munit.FunSuite:

  var server: HttpServer = scala.compiletime.uninitialized
  var baseUrl: String = scala.compiletime.uninitialized

  override def beforeAll(): Unit =
    server = HttpServer.create(InetSocketAddress(0), 0).nn
    val port = server.getAddress.nn.getPort

    server.createContext("/ok", (ex: HttpExchange) =>
      val body = "hello"
      ex.sendResponseHeaders(200, body.length)
      val os = ex.getResponseBody.nn
      os.write(body.getBytes)
      os.close()
    )

    server.createContext("/echo", (ex: HttpExchange) =>
      val input = String(ex.getRequestBody.nn.readAllBytes())
      ex.sendResponseHeaders(200, input.length)
      val os = ex.getResponseBody.nn
      os.write(input.getBytes)
      os.close()
    )

    server.createContext("/not-found", (ex: HttpExchange) =>
      val body = """{"error": "not found"}"""
      ex.sendResponseHeaders(404, body.length)
      val os = ex.getResponseBody.nn
      os.write(body.getBytes)
      os.close()
    )

    server.createContext("/server-error", (ex: HttpExchange) =>
      val body = "internal server error: something broke"
      ex.sendResponseHeaders(500, body.length)
      val os = ex.getResponseBody.nn
      os.write(body.getBytes)
      os.close()
    )

    server.start()
    baseUrl = s"http://localhost:$port"

  override def afterAll(): Unit =
    server.stop(0)

  test("httpGet returns response body on success"):
    given Network = Network(Set("localhost"))
    val result = WebOps.httpGet(s"$baseUrl/ok")
    assertEquals(result, "hello")

  test("httpPost sends body and returns response"):
    given Network = Network(Set("localhost"))
    val result = WebOps.httpPost(s"$baseUrl/echo", "ping", "text/plain")
    assertEquals(result, "ping")

  test("httpGet returns error body on 404"):
    given Network = Network(Set("localhost"))
    val result = WebOps.httpGet(s"$baseUrl/not-found")
    assert(result.contains("not found"), s"Expected error body, got: $result")

  test("httpGet returns error body on 500"):
    given Network = Network(Set("localhost"))
    val result = WebOps.httpGet(s"$baseUrl/server-error")
    assert(result.contains("something broke"), s"Expected error body, got: $result")

  test("httpGet rejects host not in allowed set"):
    given Network = Network(Set("example.com"))
    val ex = intercept[SecurityException]:
      WebOps.httpGet(s"$baseUrl/ok")
    assert(ex.getMessage.nn.contains("localhost"))

  test("httpGet rejects URL with no host"):
    given Network = Network(Set("localhost"))
    val ex = intercept[SecurityException]:
      WebOps.httpGet("file:///etc/passwd")
    assert(ex.getMessage.nn.contains("no host"))

  test("httpPost returns error body on 404"):
    given Network = Network(Set("localhost"))
    val result = WebOps.httpPost(s"$baseUrl/not-found", "{}", "application/json")
    assert(result.contains("not found"), s"Expected error body, got: $result")
