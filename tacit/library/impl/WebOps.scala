package tacit.library

import language.experimental.captureChecking

import java.net.{URI, HttpURLConnection}
import java.nio.charset.StandardCharsets

object WebOps:
  private val TimeoutMs = 10000

  /** Parses `url` once and validates its host against `net`. Returns the parsed
   *  URI so the subsequent request dials the same URL the check approved
   *  (parsing twice is a foot-gun: the two parses could disagree). */
  private def validatedUri(url: String)(using net: Network): URI =
    val uri = URI(url)
    val host = uri.getHost
    if host == null then throw SecurityException(s"Invalid URL (no host): $url")
    net.validateHost(host)
    uri

  /** Opens a connection with redirects disabled — otherwise an allowlisted host
   *  replying `302 Location: http://internal/…` would tunnel requests to a host
   *  the allowlist never saw (SSRF). Callers must `disconnect()` the result. */
  private def openConnection(uri: URI): HttpURLConnection =
    val conn = uri.toURL.openConnection().asInstanceOf[HttpURLConnection]
    conn.setInstanceFollowRedirects(false)
    conn.setConnectTimeout(TimeoutMs)
    conn.setReadTimeout(TimeoutMs)
    conn

  /** Reads the response body, falling back to the error stream on HTTP error codes. */
  private def readResponse(conn: HttpURLConnection): String =
    val code = conn.getResponseCode
    val stream = if code >= 400 then conn.getErrorStream else conn.getInputStream
    if stream == null then s"HTTP $code (no response body)"
    else
      try String(stream.readAllBytes(), StandardCharsets.UTF_8)
      finally stream.close()

  def httpGet(url: String)(using net: Network): String =
    val conn = openConnection(validatedUri(url))
    try
      conn.setRequestMethod("GET")
      readResponse(conn)
    finally conn.disconnect()

  def httpPost(url: String, body: String, contentType: String)(using net: Network): String =
    val conn = openConnection(validatedUri(url))
    try
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setRequestProperty("Content-Type", contentType)
      val os = conn.getOutputStream
      try os.write(body.getBytes(StandardCharsets.UTF_8))
      finally os.close()
      readResponse(conn)
    finally conn.disconnect()
