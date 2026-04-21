package tacit

import tacit.mcp.*
import tacit.core.*
import Context.*
import Log.*

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.PrintWriter
import scala.util.control.NonFatal

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
      val stdinLines = scala.io.Source.fromInputStream(System.in).getLines()
      val writer = PrintWriter(jsonRpcOut, true)

      if !config.quiet then printStartupBanner(config)

      try
        for line <- stdinLines if line.trim.nonEmpty do
          try handleLine(line, writer, server)
          catch
            case NonFatal(e) =>
              error(s"Request failed: ${e.getMessage}")
              e.printStackTrace(System.err)
      finally log("Server shutting down...")

private def handleLine(line: String, writer: PrintWriter, server: McpServer)(using Context): Unit =
  log(s"Received: ${line.take(200)}...")
  parse(line) match
    case Left(err) =>
      sendResponse(writer, JsonRpcResponse.error(None, JsonRpcError.ParseError,
        s"Parse error: ${err.message}"))
    case Right(json) => json.as[JsonRpcRequest] match
      case Left(err) =>
        sendResponse(writer, JsonRpcResponse.error(None, JsonRpcError.InvalidRequest,
          s"Invalid request: ${err.message}"))
      case Right(request) =>
        server.handleRequest(request).foreach(sendResponse(writer, _))

private def printStartupBanner(config: Config): Unit =
  val jarPath = scala.util.Try(
    java.io.File(classOf[McpServer].getProtectionDomain.getCodeSource.getLocation.toURI).getAbsolutePath
  ).getOrElse("<path/to/TACIT-assembly.jar>")
  val cwd = System.getProperty("user.dir")
  val recordingStatus = config.recordPath match
    case Some(dir) => s"Recording: ON -> $dir"
    case None      => "Recording: OFF"
  val sessionStatus = if config.sessionEnabled then "Sessions:  ON" else "Sessions:  OFF"
  val libConfigStr = config.libraryConfig.spaces2
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

