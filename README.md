# TACIT — Tracked Agent Capabilities In Types

**Paper:** [Tracking Capabilities for Safer Agents (arXiv:2603.00991)](https://arxiv.org/abs/2603.00991)

An [MCP](https://modelcontextprotocol.io/) server that executes Scala 3 code in a sandboxed environment via an embedded REPL. It enforces security through a capability-based system using Scala 3's experimental [capture checking](https://nightly.scala-lang.org/docs/reference/experimental/capture-checking/), preventing unauthorized access to the file system, processes, and network.

Supports both stateless one-shot execution and stateful sessions that persist definitions across calls.

## Project Structure

The project is split into two sbt subprojects that are packaged as **separate JARs**:

| Subproject | Directory | JAR | Purpose |
|---|---|---|---|
| `root` | `src/` | `TACIT-assembly-*.jar` | MCP server, executor, validator |
| `lib` | `library/` | `TACIT-library.jar` | Capability-safe API (file system, process, network, LLM) |

This separation allows you to **modify and extend the library independently** from the server — for example, adding new APIs for your agents — and swap in different library JARs without rebuilding the server.

## Quick Start

### Build

```bash
# Build both JARs
sbt "lib/assembly"   # → library/target/scala-*/TACIT-library.jar
sbt assembly          # → target/scala-*/TACIT-assembly-*.jar
```

### Run

Since the server and library are separate JARs, you need both on the classpath:

```bash
java -cp target/scala-*/TACIT-assembly-*.jar:library/target/scala-*/TACIT-library.jar \
  tacit.TACIT --library-jar library/target/scala-*/TACIT-library.jar
```

- **`-cp`** puts both JARs on the JVM classpath so the server can load library types at startup.
- **`--library-jar`** (required) tells the server where the library JAR is so it can be added to the embedded REPL's classpath (the REPL runs user code and needs access to the capability API).

To enable execution logging:

```bash
java -cp server.jar:library.jar tacit.TACIT \
  --library-jar library.jar --record ./log
```

To use a JSON config file:

```bash
java -cp server.jar:library.jar tacit.TACIT \
  --library-jar library.jar --config config.json
```

## MCP Client Configuration

**Claude Desktop** (`~/.config/claude/claude_desktop_config.json`):

```json
{
  "mcpServers": {
    "scala-exec": {
      "command": "java",
      "args": [
        "-cp", "/path/to/TACIT-assembly.jar:/path/to/TACIT-library.jar",
        "tacit.TACIT",
        "--library-jar", "/path/to/TACIT-library.jar"
      ]
    }
  }
}
```

<details>
<summary>Using sbt directly (for development)</summary>

Build the library JAR first (`sbt "lib/assembly"`), then point `sbt run` at it:

```json
{
  "mcpServers": {
    "scala-exec": {
      "command": "sbt",
      "args": ["--error", "run -- --library-jar library/target/scala-3.8.0-RC1-NIGHTLY/TACIT-library.jar"],
      "cwd": "/path/to/TACIT"
    }
  }
}
```

</details>

## Tools

| Tool | Parameters | Description |
|------|-----------|-------------|
| `execute_scala` | `code` | Execute a Scala snippet in a fresh REPL (stateless) |
| `create_repl_session` | - | Create a persistent REPL session, returns `session_id` |
| `execute_in_session` | `session_id`, `code` | Execute code in an existing session (stateful) |
| `list_sessions` | - | List active session IDs |
| `delete_repl_session` | `session_id` | Delete a session |
| `show_interface` | - | Show the full capability API reference |

### Example: Stateful Session

```
1. create_repl_session          → session_id: "abc-123"
2. execute_in_session(code: "val x = 42")   → x: Int = 42
3. execute_in_session(code: "x * 2")        → val res0: Int = 84
4. delete_repl_session(session_id: "abc-123")
```

## Security Model

All user code is validated before execution. Direct use of `java.io`, `java.nio`, `java.net`, `ProcessBuilder`, `Runtime.getRuntime`, reflection APIs, and other unsafe APIs is rejected. Instead, users access these resources through a capability-based API that is automatically injected into every REPL session.

### Capability API

The API exposes three capability request methods, each scoping access to a block:

```scala
// File system: scoped to a root directory
requestFileSystem("/tmp/work") {
  val f = access("data.txt")
  f.write("hello")
  val lines = f.readLines()
  grep("data.txt", "hello")
  find(".", "*.txt")
}

// Process execution: scoped to an allowlist of commands
requestExecPermission(Set("ls", "cat")) {
  val result = exec("ls", List("-la"))
  println(result.stdout)
}

// Network: scoped to an allowlist of hosts
requestNetwork(Set("api.example.com")) {
  val body = httpGet("https://api.example.com/data")
  httpPost("https://api.example.com/submit", """{"key":"value"}""")
}
```

Capabilities cannot escape their scoped block: this is enforced at compile time by Scala 3's capture checker.

### LLM

A secondary LLM is available through the `chat` method, no capability scope required. Safety comes from the `Classified` type system: `chat(String): String` for regular data, `chat(Classified[String]): Classified[String]` for sensitive data.

```scala
// Regular chat
val answer = chat("What is 2 + 2?")

// Classified chat: input and output stay wrapped
requestFileSystem("/secrets") {
  val secret = readClassified("/secrets/key.txt")
  val result = chat(secret.map(s => s"Summarize: $s"))
  // result is Classified[String], cannot be printed or leaked
}
```

Configure via CLI flags (`--llm-base-url`, `--llm-api-key`, `--llm-model`) or a JSON config file (`--config`). Any OpenAI-compatible API is supported.

### Validation

Code is checked against 40+ forbidden patterns before execution, covering:

- File I/O bypass (`java.io.*`, `java.nio.*`, `scala.io.*`)
- Process bypass (`ProcessBuilder`, `Runtime.getRuntime`, `scala.sys.process`)
- Network bypass (`java.net.*`, `javax.net.*`, `HttpClient`)
- Reflection (`getDeclaredMethod`, `setAccessible`, `Class.forName`)
- JVM internals (`sun.misc.*`, `jdk.internal.*`)
- Capture checking escape (`caps.unsafe`, `unsafeAssumePure`, `.asInstanceOf`)
- System control (`System.exit`, `System.setProperty`, `new Thread`)
- Class loading (`ClassLoader`, `URLClassLoader`)

## Extending the Library: Adding Your Own API

The library (`library/`) defines the capability API that user code can call inside the REPL. You can add new capabilities (e.g., database access, message queues, custom services) by modifying the library and rebuilding just the library JAR.

### Library Structure

```
library/
├── Interface.scala          # Public API trait — what user code sees
├── impl/
│   ├── InterfaceImpl.scala  # Wires everything together (exports Ops objects)
│   ├── FileOps.scala        # grep, grepRecursive, find
│   ├── ProcessOps.scala     # exec, execOutput
│   ├── WebOps.scala         # httpGet, httpPost
│   ├── LlmOps.scala         # chat
│   ├── RealFileSystem.scala # FileSystem on real disk
│   ├── VirtualFileSystem.scala # In-memory FileSystem (for testing)
│   ├── ClassifiedImpl.scala # Classified[T] wrapper implementation
│   ├── CommandValidator.scala  # Command allowlist enforcement
│   └── LlmConfig.scala     # LLM configuration case class
└── test/                    # Library-level tests
```

### Step-by-Step: Adding a New API

Here is an example of adding a hypothetical `requestDatabase` capability.

#### 1. Define types and capability in `Interface.scala`

```scala
// Add a result type
case class QueryResult(columns: List[String], rows: List[List[String]])

// Add a capability class
class DatabasePermission(val connectionString: String) extends caps.SharedCapability

// Add methods to the Interface trait
trait Interface:
  // ... existing methods ...

  def requestDatabase[T](connectionString: String)(op: DatabasePermission^ ?=> T)(using IOCapability): T

  def query(sql: String)(using DatabasePermission): QueryResult
```

Key points:
- The capability class **must extend `caps.SharedCapability`**. This is what enables Scala 3's capture checker to prevent the capability from escaping its scoped block.
- The `request*` method takes a block `op` that receives the capability as a context parameter (`?=>`). The `^` mark means the capability is tracked by the capture checker.
- Operation methods (like `query`) take the capability as a `using` parameter, so they can only be called inside the corresponding `request*` block.

#### 2. Implement the operations in `impl/`

Create `library/impl/DatabaseOps.scala`:

```scala
package tacit.library

import language.experimental.captureChecking

object DatabaseOps:
  def query(sql: String)(using perm: DatabasePermission): QueryResult =
    // Your implementation here
    // perm.connectionString has the connection info
    ???
```

#### 3. Wire it into `InterfaceImpl`

In `library/impl/InterfaceImpl.scala`, export your new operations and implement the `request*` method:

```scala
class InterfaceImpl(...) extends Interface:
  export FileOps.*
  export ProcessOps.*
  export WebOps.*
  export DatabaseOps.*   // ← add this

  // ... existing methods ...

  def requestDatabase[T](connectionString: String)(op: DatabasePermission^ ?=> T)(using IOCapability): T =
    val perm = new DatabasePermission(connectionString)
    op(using perm)
```

#### 4. Block direct access in the validator (server side)

If your new API wraps a Java/Scala library that users should not call directly, add forbidden patterns to `src/main/scala/executor/CodeValidator.scala`:

```scala
ForbiddenPattern("db-jdbc", raw"java\.sql\b".r, "Direct JDBC access is forbidden; use requestDatabase"),
ForbiddenPattern("db-driver", raw"DriverManager".r, "DriverManager is forbidden; use requestDatabase"),
```

This ensures user code goes through the capability API instead of bypassing it.

#### 5. Add dependencies (if needed)

If your new API requires external libraries, add them to the `lib` project in `build.sbt`:

```scala
lazy val lib = project
  .in(file("library"))
  .settings(
    // ... existing settings ...
    libraryDependencies ++= Seq(
      "com.openai" % "openai-java" % "4.23.0",
      "org.postgresql" % "postgresql" % "42.7.3",  // ← add your dep
    ),
  )
```

#### 6. Rebuild the library JAR

```bash
sbt "lib/assembly"
```

You do **not** need to rebuild the server JAR unless you changed `CodeValidator` (step 4) or other server-side code. Just point the server at the new library JAR:

```bash
java -cp server.jar:new-library.jar tacit.TACIT --library-jar new-library.jar
```

### Things to Keep in Mind

- **Capabilities must extend `caps.SharedCapability`.** This is what makes capture checking work — without it, the compiler cannot track the capability's scope and users could leak it out of the `request*` block.

- **Capture checking is experimental.** The project uses `-language:experimental.captureChecking`. Compiler behavior may change across Scala 3 nightly versions. If you hit unexpected errors, check if the issue is with capture checking by temporarily removing the flag.

- **The library uses Scala 3 nightly.** The build automatically fetches the latest Scala 3 nightly. This means your code must be compatible with bleeding-edge Scala. Pin a specific version in `build.sbt` (`val scala3Version = "3.x.y"`) if you need stability.

- **`Interface.scala` is bundled as a resource.** The server copies `Interface.scala` into its resources at build time so the `show_interface` tool can display it. If you add new APIs, users will see them via `show_interface` automatically — no extra work needed.

- **Forbidden patterns run on user code, not library code.** The validator in `CodeValidator.scala` only checks user-submitted code. The library itself can freely use `java.io`, `java.net`, `ProcessBuilder`, etc. in its implementation. But if your new API wraps a Java API, you should add a corresponding forbidden pattern so users cannot bypass your capability wrapper.

- **The library JAR is a fat JAR.** `sbt "lib/assembly"` produces a JAR that includes all of the library's dependencies (e.g., `openai-java`). If you add a dependency, it will be bundled automatically.

- **Server depends on library types at compile time.** The server imports `library.LlmConfig` in its `Config.scala`. If you add new configuration types to the library that the server needs, you'll need to rebuild both JARs. For pure API additions (new capabilities, new operations), only the library JAR needs rebuilding.

- **Test your API at the library level first.** The `library/test/` directory contains library-level tests using MUnit. Test your new operations there before doing integration tests through the MCP server. See `LibrarySuite.test.scala` for examples.

## Configuration

The server can be configured via CLI flags or a JSON config file.

### CLI Flags

| Flag | Description |
|------|-------------|
| `-r`/`--record <dir>` | Log every execution to disk |
| `-s`/`--strict` | Block file ops (cat, ls, rm, etc.) through exec |
| `--classified-paths <paths>` | Comma-separated classified (protected) paths |
| `--library-jar <path>` | **Required.** Path to the library JAR (`TACIT-library.jar`) |
| `-q`/`--quiet` | Suppress startup banner and request/response logging |
| `--no-wrap` | Disable wrapping user code in `def run() = ... ; run()` |
| `--no-session` | Disable session-related tools |
| `-c`/`--config <path>` | JSON config file (flags after `--config` override file values) |
| `--llm-base-url <url>` | LLM API base URL |
| `--llm-api-key <key>` | LLM API key |
| `--llm-model <name>` | LLM model name |

### JSON Config File

```json
{
  "recordPath": "/tmp/recordings",
  "strictMode": true,
  "quiet": false,
  "wrappedCode": false,
  "sessionEnabled": true,
  "classifiedPaths": ["/home/user/secret"],
  "libraryJarPath": "/path/to/TACIT-library.jar",
  "llm": {
    "baseUrl": "https://api.openai.com",
    "apiKey": "sk-...",
    "model": "gpt-4o-mini"
  }
}
```

## Development

```bash
sbt clean                      # Clean build artifacts
sbt compile                    # Compile
sbt test                       # Run all tests
sbt "testOnly *McpServerSuite" # Run a single suite
sbt "lib/assembly"             # Build library JAR first
sbt "run -- --library-jar library/target/scala-*/TACIT-library.jar"  # Run locally
sbt "run -- --library-jar lib.jar --record ./log"       # Run with logging
sbt "run -- --library-jar lib.jar --config config.json" # Run with JSON config
sbt assembly                   # Build server fat JAR (excludes library)
sbt "lib/assembly"             # Build library fat JAR
```

## Requirements

- JDK 17+
- sbt 1.12+

## License

Apache-2.0
