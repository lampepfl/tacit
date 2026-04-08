package tacit

import tacit.mcp.*
import tacit.core.*
import Context.*
import Log.*

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.PrintWriter

/** TACIT - A Model Context Protocol server for safe Scala code execution */
@main def StartMCP(args: String*): Unit =
  // Save the real stdout for JSON-RPC before any REPL compiler can pollute it.
  // The Scala compiler (especially with capture checking) may write diagnostic
  // output directly to System.out, bypassing ReplDriver's capture stream.
  // Redirecting System.out to stderr ensures compiler noise never corrupts
  // the JSON-RPC channel.
  val jsonRpcOut = System.out
  System.setOut(System.err)

  Config.parseCliArgs(args.toArray) match
    case None =>  // Errors should have been displayed by the parser
    case Some(config) => usingContext(config):
      val server = new McpServer
      val stdinLines = scala.io.Source.fromInputStream(System.in).getLines()
      val writer = new PrintWriter(jsonRpcOut, true)

      def printStartupBanner(): Unit =
        val jarPath = scala.util.Try {
          new java.io.File(classOf[McpServer].getProtectionDomain.getCodeSource.getLocation.toURI).getAbsolutePath
        }.getOrElse("<path/to/TACIT-assembly-0.1.0-SNAPSHOT.jar>")
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
$libConfigStr
            | JAR:       $jarPath
            | CWD:       $cwd
            |""".stripMargin)

      if !config.quiet then printStartupBanner()

      try
        for line <- stdinLines if line.trim.nonEmpty do
          log(s"Received: ${line.take(200)}...")

          parse(line) match
            case Left(error) =>
              val response = JsonRpcResponse.error(
                None,
                JsonRpcError.ParseError,
                s"Parse error: ${error.message}"
              )
              sendResponse(writer, response)

            case Right(json) =>
              json.as[JsonRpcRequest] match
                case Left(error) =>
                  val response = JsonRpcResponse.error(
                    None,
                    JsonRpcError.InvalidRequest,
                    s"Invalid request: ${error.message}"
                  )
                  sendResponse(writer, response)

                case Right(request) =>
                  server.handleRequest(request).foreach { response =>
                    sendResponse(writer, response)
                  }
      catch
        case e: Exception =>
          error(e.getMessage)
          e.printStackTrace(System.err)
      finally
        log("Server shutting down...")

def sendResponse(writer: PrintWriter, response: JsonRpcResponse)(using Context): Unit =
  val json = response.asJson.noSpaces
  log(s"Sending: ${json.take(200)}...")
  writer.println(json)
  writer.flush()

