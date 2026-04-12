import io.circe.*
import io.circe.syntax.*
import io.circe.parser.*
import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

class McpServerIntegrationSuite extends munit.FunSuite:
  given ExecutionContext = ExecutionContext.global

  // Longer timeout for integration tests since REPL initialization is slow
  override val munitTimeout: Duration = 120.seconds

  case class McpClient(process: Process, writer: PrintWriter, reader: BufferedReader):
    private var requestId = 0

    def nextId(): Int =
      requestId += 1
      requestId

    def send(method: String, params: Option[Json] = None, id: Option[Int] = None): Unit =
      val request = Json.obj(
        "jsonrpc" -> "2.0".asJson,
        "method" -> method.asJson,
        "params" -> params.asJson,
        "id" -> id.asJson
      )
      val line = request.noSpaces
      writer.println(line)
      writer.flush()

    def receive(): Json =
      val line = reader.readLine()
      if line == null then
        throw new RuntimeException("Server closed connection")
      parse(line).getOrElse(throw new RuntimeException(s"Invalid JSON: $line"))

    def request(method: String, params: Option[Json] = None): Json =
      val id = nextId()
      send(method, params, Some(id))
      receive()

    def close(): Unit =
      writer.close()
      reader.close()
      process.destroy()

  def startServer(): McpClient =
    // Use the main class directly via Java to avoid sbt logging interference
    val classpath = System.getProperty("java.class.path")
    val libraryJar = System.getProperty("tacit.library.jar")
    assert(libraryJar != null, "tacit.library.jar system property not set — sbt should set this automatically")
    val processBuilder = new ProcessBuilder(
      "java", "-cp", classpath, "tacit.StartMCP", "--library-jar", libraryJar
    )
    processBuilder.directory(new java.io.File(System.getProperty("user.dir")))
    processBuilder.redirectErrorStream(false)

    val process = processBuilder.start()
    val writer = new PrintWriter(process.getOutputStream, true)
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))

    McpClient(process, writer, reader)

  test("full MCP workflow via stdio".flaky):
    val client = startServer()

    try
      // 1. Initialize
      val initResponse = client.request("initialize", Some(Json.obj(
        "protocolVersion" -> "2024-11-05".asJson,
        "capabilities" -> Json.obj(),
        "clientInfo" -> Json.obj(
          "name" -> "test-client".asJson,
          "version" -> "1.0.0".asJson
        )
      )))

      assert(initResponse.hcursor.downField("result").get[String]("protocolVersion").isRight)
      assert(initResponse.hcursor.downField("result").downField("serverInfo").get[String]("name").toOption.contains("TACIT MCP"))

      // Send initialized notification
      client.send("initialized")

      // 2. List tools — should be exactly one
      val toolsResponse = client.request("tools/list")
      val tools = toolsResponse.hcursor.downField("result").get[List[Json]]("tools")
      assert(tools.isRight)
      val toolNames = tools.toOption.get.flatMap(_.hcursor.get[String]("name").toOption)
      assertEquals(toolNames, List("eval_scala"))

      // 3. Evaluate simple Scala code
      val execResponse = client.request("tools/call", Some(Json.obj(
        "name" -> "eval_scala".asJson,
        "arguments" -> Json.obj(
          "code" -> "1 + 1".asJson
        )
      )))

      val execContent = execResponse.hcursor
        .downField("result")
        .get[List[Json]]("content")
        .toOption.get
        .head
        .hcursor.get[String]("text").toOption.get

      assert(execContent.contains("2"), s"Expected output to contain '2', got: $execContent")

      // 4. Define a variable
      val defResponse = client.request("tools/call", Some(Json.obj(
        "name" -> "eval_scala".asJson,
        "arguments" -> Json.obj(
          "code" -> "val x = 42".asJson
        )
      )))
      val defError = defResponse.hcursor.downField("error").focus
      assert(defError.isEmpty || defError.exists(_.isNull), s"Unexpected error: $defResponse")

      // 5. Use variable (proves default session keeps state)
      val useResponse = client.request("tools/call", Some(Json.obj(
        "name" -> "eval_scala".asJson,
        "arguments" -> Json.obj(
          "code" -> "x * 2".asJson
        )
      )))

      val useContent = useResponse.hcursor
        .downField("result")
        .get[List[Json]]("content")
        .toOption.get
        .head
        .hcursor.get[String]("text").toOption.get

      assert(useContent.contains("84"), s"Expected '84' in output, got: $useContent")

    finally
      client.close()
