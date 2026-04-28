# Capability-Safe Execution Library

A Scala 3 library that provides sandboxed access to the file system, processes, and network through scoped capabilities. Uses Scala's experimental capture checking to enforce at compile time that capabilities cannot leak out of their granted scope.

## Capabilities

| Capability          | Granted by                | Operations                                      |
|---------------------|---------------------------|-------------------------------------------------|
| `FileSystem`        | `requestFileSystem`       | `access`, `grep`, `grepRecursive`, `find`       |
| `ProcessPermission` | `requestExecPermission`   | `exec`, `execOutput`                            |
| `Network`           | `requestNetwork`          | `httpGet`, `httpPost`                            |

Each `request*` method takes an allowlist (root path, command names, or host names) and a scoped block. The capability is only available inside that block.

## Usage

```scala
import library.*

val api: Interface = ???
import api.*
// The interface instance is normally assumed to be in scope and imported

// File system - scoped to a directory
requestFileSystem("/home/user/project") {
  val content = access("/home/user/project/README.md").read()
  val hits = grep("/home/user/project/Main.scala", "TODO")
}

// Process execution - scoped to allowed commands
requestExecPermission(Set("ls", "cat")) {
  val result = exec("ls", List("-la"))
  println(result.stdout)
}

// A command must both be in the scope set AND satisfy the host-level policy.
// The host policy is one of:
//   - commandPermissions (recommended): glob allowlist, e.g. ["echo", "py*"]
//   - strictMode         (default): blocks built-in file-op commands (cat, ls, rm, ...).
//     Convenient for quick experiments; configure commandPermissions in real use.

// Network requests have the same layering: a host must be in the scope set
// AND, if `networkPermissions` is configured, match one of its glob patterns
// (e.g. ["*.example.com", "api.github.com"]).

// Network - scoped to allowed hosts
requestNetwork(Set("api.example.com")) {
  val body = httpGet("https://api.example.com/data")
}
```

Capabilities can be nested:

```scala
requestFileSystem("/tmp/out") {
  requestNetwork(Set("api.example.com")) {
    val data = httpGet("https://api.example.com/data")
    access("/tmp/out/result.json").write(data)
  }
}
```

## Project Structure

```
Interface.scala        # Public API: trait Interface, capabilities, data types
impl/                  # Implementation
test/                  # Test suites
```

## Compile and Test

```bash
scala compile .
scala test .
```

Note, the library tests does not enable safe mode, because they are mainly testing the behaviours.