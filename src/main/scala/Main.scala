package tacit

import tacit.mcp.*
import tacit.core.*
import Context.*
import Log.*

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.nio.charset.StandardCharsets
import scala.util.control.NonFatal

/** Maximum accepted stdin line length, in characters (~16 MiB). JSON-RPC
  * messages arrive one per line; an unbounded read would let a single
  * multi-GB line exhaust server memory before parsing even starts. */
private val MaxLineChars = 16 * 1024 * 1024

/** TACIT — a Model Context Protocol server for safe Scala code execution. */
@main def StartMCP(args: String*): Unit =
  // Save the real stdout for JSON-RPC before any REPL compiler can pollute it.
  // The Scala compiler (especially with capture checking) may write diagnostics
  // directly to System.out, bypassing ReplDriver's capture stream. Redirecting
  // System.out to stderr ensures compiler noise never corrupts JSON-RPC.
  val jsonRpcOut = System.out
  System.setOut(System.err)

  Config.parseCliArgs(args.toArray) match
    case None => ()  // errors already displayed by the parser
    case Some(config) => usingContext(config):
      val server = McpServer()
      val reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
      val writer = PrintWriter(jsonRpcOut, true)

      if !config.quiet then printStartupBanner(config)

      try
        var running = true
        while running do
          readBoundedLine(reader, MaxLineChars) match
            case None =>
              running = false
            case Some(Left(())) =>
              // Over-long line: the prefix was discarded up to the newline, so
              // the stream is positioned at the next request. The id is
              // unknowable here — respond with `id: null` and keep serving.
              sendResponse(writer, JsonRpcResponse.error(None, JsonRpcError.ParseError,
                s"Parse error: request exceeds the $MaxLineChars-character line limit"))
            case Some(Right(line)) =>
              if line.trim.nonEmpty then
                try handleLine(line, writer, server)
                catch
                  case NonFatal(e) =>
                    // The response path itself failed (e.g. broken pipe); no
                    // response can be sent, so just log and keep the loop alive.
                    error(s"Failed to handle request: ${e.getMessage}")
                    e.printStackTrace(System.err)
      finally log("Server shutting down...")

/** Read one line from `reader`, retaining at most `limit` characters.
  *
  * Returns `None` on EOF (no characters seen), `Some(Right(line))` for a
  * normal line, and `Some(Left(()))` when the line exceeded the limit; in
  * that case the over-long prefix is discarded and reading resumes after the
  * terminating newline, so the stream stays in sync. A trailing `\r` (CRLF)
  * is not included in the returned line.
  */
private def readBoundedLine(reader: BufferedReader, limit: Int): Option[Either[Unit, String]] =
  val sb = StringBuilder()
  var overflow = false
  var sawAny = false
  var done = false
  while !done do
    reader.read() match
      case -1 =>
        done = true
      case '\n' =>
        sawAny = true
        done = true
      case c =>
        sawAny = true
        if !overflow && sb.length >= limit then overflow = true
        if !overflow then sb.append(c.toChar)
  if sb.nonEmpty && sb.last == '\r' then sb.setLength(sb.length - 1)
  if !sawAny then None
  else if overflow then Some(Left(()))
  else Some(Right(sb.toString))

private def handleLine(line: String, writer: PrintWriter, server: McpServer)(using Context): Unit =
  log(s"Received: ${line.take(200)}...")
  parse(line) match
    case Left(err) =>
      sendResponse(writer, JsonRpcResponse.error(None, JsonRpcError.ParseError,
        s"Parse error: ${err.message}"))
    case Right(json) => json.as[JsonRpcRequest] match
      case Left(err) =>
        // Best-effort id recovery so the client can correlate the error; an
        // absent or null id serializes as `"id": null` per spec.
        val id = json.hcursor.downField("id").focus.filterNot(_.isNull)
        sendResponse(writer, JsonRpcResponse.error(id, JsonRpcError.InvalidRequest,
          s"Invalid request: ${err.message}"))
      case Right(request) =>
        try server.handleRequest(request).foreach(sendResponse(writer, _))
        catch
          case NonFatal(e) =>
            // Never leave the client hanging: report the failure as a JSON-RPC
            // internal error tied to the request id.
            error(s"Request failed: ${e.getMessage}")
            e.printStackTrace(System.err)
            sendResponse(writer, JsonRpcResponse.error(request.id, JsonRpcError.InternalError,
              s"Internal error: ${e.getMessage}"))

private def printStartupBanner(config: Config): Unit =
  val jarPath = scala.util.Try(
    java.io.File(classOf[McpServer].getProtectionDomain.getCodeSource.getLocation.toURI).getAbsolutePath
  ).getOrElse("<path/to/TACIT-assembly.jar>")
  val cwd = System.getProperty("user.dir")
  val recordingStatus = config.recordPath match
    case Some(dir) => s"Recording: ON -> $dir"
    case None      => "Recording: OFF"
  val sessionStatus = if config.sessionEnabled then "Sessions:  ON" else "Sessions:  OFF"
  val libConfigStr = config.redactedLibraryConfig.spaces2
    .linesIterator.map(l => s"             $l").mkString("\n")
  System.err.println(
    s"""
       | TACIT MCP Server
       | Transport: stdio (JSON-RPC 2.0)
       | Protocol:  Model Context Protocol (MCP)
       | $recordingStatus
       | $sessionStatus
       | Library:   ${config.libraryJarPath}
       | LibConfig:
       | $libConfigStr
       | JAR:       $jarPath
       | CWD:       $cwd
       |""".stripMargin)

private def sendResponse(writer: PrintWriter, response: JsonRpcResponse)(using Context): Unit =
  val json = response.asJson.noSpaces
  log(s"Sending: ${json.take(200)}...")
  writer.println(json)
  writer.flush()
