package tacit.library

import language.experimental.captureChecking
import caps.*

// ─── Classified Data ────────────────────────────────────────────────────────

/** Wrapper that protects sensitive data from accidental disclosure.
 *
 *  - `toString` never reveals the underlying value (prints `Classified(****)`)
 *  - `map`/`flatMap` only accept **pure** functions, preventing side-channel leaks
 */
@assumeSafe
abstract class Classified[+T] private[library] ():
  def map[B](op: T ->{any.rd} B): Classified[B]
  def flatMap[B](op: T ->{any.rd} Classified[B]): Classified[B]

// ─── File System ────────────────────────────────────────────────────────────

/** Handle to a file or directory, obtained via `access(path)` inside a
 *  `requestFileSystem` block. Cannot escape the block scope. */
@assumeSafe
abstract class FileEntry private[library] (tracked val origin: FileSystem):
  def path: String
  def name: String
  def exists: Boolean
  def isDirectory: Boolean
  def size: Long
  def read(): String
  def readBytes(): Array[Byte]
  def write(content: String): Unit
  def append(content: String): Unit
  def readLines(): List[String]
  /** Process each line without loading the entire file into memory.
   *  The callback receives the line content and its 1-based line number. */
  def forEachLine(op: (String, Int) => Unit): Unit
  def delete(): Unit
  /** Create this path as a directory, including any missing parent directories. */
  def mkdir(): Unit
  /** List immediate children of a directory. */
  def children: List[FileEntry^{this}]
  /** Recursively list all descendants (files and subdirectories). */
  def walk(): List[FileEntry^{this}]
  /** Whether this file is under a classified (protected) path. */
  def isClassified: Boolean
  /** Read a classified file, returning its content wrapped in `Classified`.
   *  Throws `SecurityException` if the file is not under a classified path. */
  def readClassified(): Classified[String]
  /** Write classified content to a classified file.
   *  Throws `SecurityException` if the file is not under a classified path,
   *  or if the server configuration has disabled classified writes. */
  def writeClassified(content: Classified[String]): Unit

/** Capability granting access to a file-system subtree.
 *  Obtained via `requestFileSystem(root)`. */
@assumeSafe
abstract class FileSystem private[library] () extends caps.SharedCapability:
  def access(path: String): FileEntry^{this}

// ─── Data Types ─────────────────────────────────────────────────────────────

/** A single match returned by `grep` or `grepRecursive`. */
@assumeSafe
case class GrepMatch(file: String, lineNumber: Int, line: String)

/** The result of running a process via `exec`. */
@assumeSafe
case class ProcessResult(exitCode: Int, stdout: String, stderr: String)

/** The result of an HTTP request issued via `httpRequest`. */
@assumeSafe
case class HttpResponse(status: Int, body: String)

// ─── Capabilities ───────────────────────────────────────────────────────────

/** Capability granting access to a set of network hosts.
 *  Obtained via `requestNetwork(hosts)`.
 *
 *  `validateHost` throws `SecurityException` if a host is not permitted by the
 *  scope or by the host-level policy configured on the server. */
@assumeSafe
abstract class Network private[library] () extends caps.SharedCapability:
  def validateHost(host: String): Unit

/** Capability granting permission to run a set of commands.
 *  Obtained via `requestExecPermission(commands)`.
 *
 *  `validateCommand` throws `SecurityException` if a command is not permitted
 *  by the scope or by the host-level policy configured on the server. */
@assumeSafe
abstract class ProcessPermission private[library] () extends caps.SharedCapability:
  def validateCommand(command: String, args: List[String] = List.empty): Unit

/** Capability gating access to standard output (`println`, `print`, `printf`).
 *  An implicit instance is available at the REPL top level. */
@assumeSafe
class IOCapability private[library] extends caps.SharedCapability

// ─── Interface ──────────────────────────────────────────────────────────────

/** The API for interacting with the host system. All the functions are pre-loaded
 *  at the REPL top level.
 *
 *  == Example: basic file operations (write, read, list) ==
 *
 *  ```
 *  requestFileSystem("/tmp/demo") {
 *    // Access a file or directory via `access()`
 *    val f = access("/tmp/demo/hello.txt")
 *    // Check file metadata
 *    println(s"Name: ${f.name}, Size: ${f.size}, Exists: ${f.exists}")
 *
 *    // Write a file
 *    f.write("Hello, World!\nLine 2")
 *    // Read it back
 *    val content = f.read()
 *    println(s"Content: $content")
 *    // Append to the file
 *    f.append("\nLine 3")
 *    // Read individual lines
 *    val lines = f.readLines()
 *    println(s"Lines: $lines")
 *
 *    // List directory contents
 *    access("/tmp/demo").children.foreach { e =>
 *      println(s"  ${e.name} (dir=${e.isDirectory}, size=${e.size})")
 *    }
 *    // Recursively list all files under the directory
 *    access("/tmp/demo").walk().foreach { e =>
 *      println(s"  ${e.path} (dir=${e.isDirectory}, size=${e.size})")
 *    }
 *  }
 *  ```
 */
@assumeSafe
trait Interface:

  // ── File System ─────────────────────────────────────────────────────

  /** Request a `FileSystem` scoped to the subtree under `root`.
   *  Paths outside `root` throw `SecurityException`.
   *
   *  ```
   *  requestFileSystem("/home/user/project") {
   *    val content = access("/home/user/project/README.md").read()
   *    println(content)
   *    access("/home/user/project/out/result.txt").write("done")
   *    access("/home/user/project/src").children.foreach(f => println(f.name))
   *  }
   *  ``` */
  def requestFileSystem[T](root: String)(op: FileSystem^ ?=> T)(using IOCapability): T

  /** Get a `FileEntry` handle for `path`. */
  def access(path: String)(using fs: FileSystem): FileEntry^{fs}

  /** Search a single file for lines matching `pattern` (regex).
   *
   *  ```
   *  val matches = grep("/project/Main.scala", "TODO")
   *  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
   *  ``` */
  def grep(path: String, pattern: String)(using FileSystem): List[GrepMatch]

  /** Recursively search files under `dir` matching `glob` for `pattern` (regex).
   *  Files under classified paths are silently skipped (their content is never
   *  read); single-file `grep` on a classified path throws instead.
   *
   *  ```
   *  val hits = grepRecursive("/project/src", "deprecated", "*.scala")
   *  hits.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
   *  ``` */
  def grepRecursive(dir: String, pattern: String, glob: String = "*")(using FileSystem): List[GrepMatch]

  /** Find all files under `dir` matching `glob`. Returns absolute paths.
   *
   *  ```
   *  val files = find("/project/src", "*.scala")
   *  ``` */
  def find(dir: String, glob: String)(using FileSystem): List[String]

  /** Read a classified file. Throws `SecurityException` if the path is not classified.
   *
   *  ```
   *  val secret: Classified[String] = readClassified("/data/secrets/key.txt")
   *  val processed = secret.map(_.trim.toUpperCase)  // pure transform OK
   *  println(processed)  // prints "Classified(****)", content protected
   *  ``` */
  def readClassified(path: String)(using FileSystem): Classified[String]

  /** Write classified content to a classified file.
   *  Throws `SecurityException` if the path is not classified, or if the
   *  server configuration has disabled classified writes.
   *
   *  ```
   *  writeClassified("/data/secrets/upper.txt", processed)
   *  ``` */
  def writeClassified(path: String, content: Classified[String])(using FileSystem): Unit

  // ── Process Execution ───────────────────────────────────────────────

  /** Request a `ProcessPermission` for the given command names.
   *
   *  ```
   *  requestExecPermission(Set("pip", "python")) {
   *    exec("pip", List("install", "."))
   *    execOutput("python", List("script.py"))
   *  }
   *  ``` */
  def requestExecPermission[T](commands: Set[String])(op: ProcessPermission^ ?=> T)(using IOCapability): T

  /** Run `command` with `args`. Returns exit code, stdout, and stderr.
   *  Throws `RuntimeException` on timeout. */
  def exec(
    command: String,
    args: List[String] = List.empty,
    workingDir: Option[String] = None,
    timeoutMs: Long = 30000
  )(using pp: ProcessPermission): ProcessResult

  /** Run `command` and return only stdout. */
  def execOutput(
    command: String,
    args: List[String] = List.empty
  )(using pp: ProcessPermission): String

  // ── Network ─────────────────────────────────────────────────────────

  /** Request a `Network` capability for the given host names.
   *
   *  ```
   *  requestNetwork(Set("api.example.com")) {
   *    val body = httpGet("https://api.example.com/v1/status")
   *    val resp = httpPost("https://api.example.com/v1/data",
   *                        """{"key": "value"}""")
   *  }
   *  ``` */
  def requestNetwork[T](hosts: Set[String])(op: Network^ ?=> T)(using IOCapability): T

  /** HTTP GET. Returns the response body. Host must be in the allowed set.
   *
   *  `headers` sets plain request headers. `secretHeaders` sets headers whose
   *  values are `Classified` (e.g. an `Authorization` token): each value is
   *  unwrapped internally and sent to the allowlisted host, but is never
   *  observable to agent code. This lets you authenticate to an allowed API
   *  with a secret read via `readClassified` without declassifying it.
   *
   *  ```
   *  requestNetwork(Set("api.example.com")) {
   *    val key = readClassified("/data/secrets/api.key")
   *    val body = httpGet("https://api.example.com/me",
   *                       secretHeaders = Map("Authorization" -> key.map("Bearer " + _)))
   *  }
   *  ``` */
  def httpGet(
    url: String,
    headers: Map[String, String] = Map.empty,
    secretHeaders: Map[String, Classified[String]] = Map.empty
  )(using net: Network): String

  /** HTTP POST with `body`. Returns the response body. See `httpGet` for the
   *  `headers`/`secretHeaders` contract. */
  def httpPost(
    url: String,
    body: String,
    contentType: String = "application/json",
    headers: Map[String, String] = Map.empty,
    secretHeaders: Map[String, Classified[String]] = Map.empty
  )(using net: Network): String

  /** Issue an HTTP request with an arbitrary `method` (GET, PUT, DELETE, PATCH,
   *  ...). Returns the status code alongside the body. `body` is sent only when
   *  non-empty. See `httpGet` for the `headers`/`secretHeaders` contract. */
  def httpRequest(
    method: String,
    url: String,
    body: String = "",
    headers: Map[String, String] = Map.empty,
    secretHeaders: Map[String, Classified[String]] = Map.empty
  )(using net: Network): HttpResponse

  /** POST a `Classified` body to an allowlisted host and receive a `Classified`
   *  response. The body is unwrapped internally (never observable to agent
   *  code), sent, and the response is re-wrapped, so sensitive data can flow
   *  through an external service while staying under information-flow control.
   *
   *  ```
   *  requestNetwork(Set("api.example.com")) {
   *    val secret = readClassified("/data/secrets/payload.json")
   *    val reply: Classified[String] = httpPostClassified("https://api.example.com/process", secret)
   *  }
   *  ``` */
  def httpPostClassified(
    url: String,
    body: Classified[String],
    contentType: String = "application/json",
    headers: Map[String, String] = Map.empty,
    secretHeaders: Map[String, Classified[String]] = Map.empty
  )(using net: Network): Classified[String]

  // ── print ─────────────────────────────────────────────────────────-

  /** Print to the standard output stream visible to the agent.
   *
   *  If you want to show results containing classified data to the end user,
   *  print the classified value in a separate print call so it can be captured by
   *  the secure output sink. When the argument is a `Classified[_]`, only the masked form
   *  `Classified(***)` is written here, and the actual content is never shown
   *  to the agent. When the host has configured a secure output sink, the
   *  unwrapped content is additionally written to that sink, which only
   *  the end user can read. Non-classified arguments are printed normally
   *  in both places. */
  def println(x: Any)(using IOCapability): Unit
  def println()(using IOCapability): Unit
  /** See `println(x)` for the classified-data handling contract. */
  def print(x: Any)(using IOCapability): Unit
  /** See `println(x)` for the classified-data handling contract. */
  def printf(fmt: String, args: Any*)(using IOCapability): Unit

  // ── Classified ──────────────────────────────────────────────────────

  /** Wrap a value in `Classified` to protect it from disclosure. */
  def classify[T](value: T): Classified[T]

  // ── LLM ─────────────────────────────────────────────────────────────

  /** Send a message to the configured LLM. No capability scope required.
   *  Throws `RuntimeException` if no LLM is configured.
   *
   *  ```
   *  val answer = chat("What is the capital of Switzerland?")
   *  ``` */
  def chat(message: String): String

  /** Send a classified message. Returns a classified response.
   *
   *  ```
   *  val secret = readClassified("/data/secrets/question.txt")
   *  val summary: Classified[String] = chat(secret.map(q => s"Summarize the following: $q"))
   *  ``` */
  def chat(message: Classified[String]): Classified[String]
