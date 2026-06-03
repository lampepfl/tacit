package tacit.executor

import scala.util.matching.Regex

/** A violation found by the code validator. */
case class ValidationViolation(
  ruleId: String,
  description: String,
  lineNumber: Int,
  snippet: String
)

/** Validates user code against forbidden patterns before REPL execution. */
object CodeValidator:

  private case class ForbiddenPattern(
    id: String,
    regex: Regex,
    description: String
  )

  private val forbiddenPatterns: List[ForbiddenPattern] = List(
    // File IO bypass
    ForbiddenPattern("file-io-java", raw"java\.io\b".r, "Direct java.io access is forbidden; use requestFileSystem"),
    ForbiddenPattern("file-io-nio", raw"java\.nio\b".r, "Direct java.nio access is forbidden; use requestFileSystem"),
    ForbiddenPattern("file-io-scala", raw"scala\.io\b".r, "Direct scala.io access is forbidden; use requestFileSystem"),

    // Process bypass
    ForbiddenPattern("proc-builder", raw"ProcessBuilder".r, "ProcessBuilder is forbidden; use requestExecPermission"),
    ForbiddenPattern("proc-runtime", raw"Runtime\.getRuntime".r, "Runtime.getRuntime is forbidden; use requestExecPermission"),
    // `scala` is auto-imported, so `scala.sys.process` is reachable as plain
    // `sys.process` too; anchor on `sys.` so both spellings are covered.
    ForbiddenPattern("proc-scala", raw"\bsys\.process\b".r, "scala.sys.process is forbidden; use requestExecPermission"),

    // Network bypass
    ForbiddenPattern("net-java", raw"java\.net\b".r, "Direct java.net access is forbidden; use requestNetwork"),
    ForbiddenPattern("net-javax", raw"javax\.net\b".r, "Direct javax.net access is forbidden; use requestNetwork"),
    ForbiddenPattern("net-http-client", raw"HttpClient".r, "HttpClient is forbidden; use requestNetwork"),
    ForbiddenPattern("net-http-conn", raw"HttpURLConnection".r, "HttpURLConnection is forbidden; use requestNetwork"),

    // Cast escape (whitespace before `[` is legal Scala and would otherwise evade us).
    ForbiddenPattern("cast-escape", raw"\.asInstanceOf\s*\[".r, ".asInstanceOf is forbidden"),

    // CC unsafe
    ForbiddenPattern("cc-unsafe-caps", raw"caps\.unsafe".r, "caps.unsafe explicitly escapes capture checking"),
    ForbiddenPattern("cc-unsafe-pure", raw"unsafeAssumePure".r, "unsafeAssumePure explicitly escapes capture checking"),

    // Reflection
    ForbiddenPattern("reflect-method", raw"getDeclaredMethod".r, "Reflective access is forbidden"),
    ForbiddenPattern("reflect-field", raw"getDeclaredField".r, "Reflective access is forbidden"),
    ForbiddenPattern("reflect-ctor", raw"getDeclaredConstructor".r, "Reflective access is forbidden"),
    ForbiddenPattern("reflect-accessible", raw"setAccessible".r, "Reflective access is forbidden"),
    ForbiddenPattern("reflect-java", raw"java\.lang\.reflect\b".r, "java.lang.reflect is forbidden"),
    ForbiddenPattern("reflect-scala", raw"scala\.reflect\.runtime".r, "scala.reflect.runtime is forbidden"),
    ForbiddenPattern("reflect-forname", raw"Class\.forName".r, "Class.forName is forbidden"),

    // JVM internals
    ForbiddenPattern("jvm-jdk-internal", raw"jdk\.internal\b".r, "jdk.internal access is forbidden"),
    ForbiddenPattern("jvm-sun", raw"\bsun\.\w+".r, "sun.* access is forbidden"),
    ForbiddenPattern("jvm-com-sun", raw"com\.sun\.\w+".r, "com.sun.* access is forbidden"),

    // IO bypass, prevent circumventing shadowed println/print
    ForbiddenPattern("io-system-out", raw"System\.out\b".r, "System.out is forbidden; use println (which requires IOCapability)"),
    ForbiddenPattern("io-system-err", raw"System\.err\b".r, "System.err is forbidden; use println (which requires IOCapability)"),
    ForbiddenPattern("io-console", raw"\bConsole\b".r, "scala.Console is forbidden; use println (which requires IOCapability)"),
    ForbiddenPattern("io-predef-print", raw"Predef\.print".r, "Predef.println/print is forbidden; use the shadowed version"),

    // System control
    ForbiddenPattern("sys-exit", raw"System\.exit".r, "System.exit is forbidden"),
    ForbiddenPattern("sys-setprop", raw"System\.setProperty".r, "System.setProperty is forbidden"),
    ForbiddenPattern("sys-getenv", raw"System\.getenv".r, "System.getenv is forbidden"),
    ForbiddenPattern("sys-getprop", raw"System\.getProperty".r, "System.getProperty is forbidden"),
    // `scala.sys` (reachable as plain `sys` since `scala` is auto-imported) re-exposes
    // the same JVM hooks System.* guards: sys.exit kills the server, sys.env / sys.props
    // read environment/properties, sys.runtime is Runtime.getRuntime.
    ForbiddenPattern("sys-scala", raw"\bsys\.(exit|env|props|runtime|allThreads|addShutdownHook)\b".r,
      "scala.sys.* (exit/env/props/runtime/...) is forbidden; use the capability API"),
    ForbiddenPattern("sys-thread", raw"\bnew\s+Thread\b".r, "Creating threads is forbidden"),

    // Directives
    ForbiddenPattern("directive-using", raw"//>\s*using".r, "//> using directives are forbidden"),
    ForbiddenPattern("directive-import", """import\s+\$""".r, "import $ directives are forbidden"),

    // Class loading
    ForbiddenPattern("classloader", raw"ClassLoader".r, "ClassLoader access is forbidden"),
    ForbiddenPattern("urlclassloader", raw"URLClassLoader".r, "URLClassLoader access is forbidden"),
    ForbiddenPattern("dotty-tools", raw"dotty\.tools\b".r, "dotty.tools access is forbidden"),
    ForbiddenPattern("scala-tools", raw"scala\.tools\b".r, "scala.tools access is forbidden"),
  )

  private inline def isIdentChar(c: Char): Boolean = Character.isLetterOrDigit(c) || c == '_'
  private inline def isIdentStart(c: Char): Boolean = Character.isLetter(c) || c == '_'

  /** Strip string literals and comments, replacing their content with spaces,
    * while PRESERVING string-interpolation expressions (`${...}` and `$ident`)
    * as code.
    *
    * Interpolation bodies are executable code, and [[ManagedRepl]] compiles the
    * *original* (unstripped) source — so a stripper that blanked `${...}` would
    * let `s"${java.lang.Runtime.getRuntime.exec(...)}"` slip past every pattern
    * yet still run. We therefore lex with a small mode stack: inside a string we
    * blank literal text, but `${...}` pushes back into a code context (where its
    * tokens are seen, and any *nested* string literal is blanked again so a
    * quoted token like `s"${ f("java.io") }"` is not a false positive).
    *
    * Newlines are preserved and every input char maps to exactly one output char,
    * so reported line numbers stay correct.
    */
  def stripLiteralsAndComments(code: String): String =
    val sb = StringBuilder(code.length)
    val len = code.length
    // A frame is either code (`isString=false`; `fromInterp` ⇒ a balanced `}`
    // closes the enclosing `${...}`) or a string (`triple`/`interp` flags).
    final class Frame(val isString: Boolean, val triple: Boolean, val interp: Boolean, val fromInterp: Boolean):
      var brace: Int = 0
    val stack = scala.collection.mutable.Stack[Frame](Frame(false, false, false, false))

    inline def emit(c: Char): Unit = sb.append(c)
    inline def blank(c: Char): Unit = sb.append(if c == '\n' then '\n' else ' ')

    var i = 0
    while i < len do
      val f = stack.top
      val c = code.charAt(i)
      if f.isString then
        val isClose =
          if f.triple then c == '"' && i + 2 < len && code.charAt(i + 1) == '"' && code.charAt(i + 2) == '"'
          else c == '"'
        if !f.triple && c == '\\' && i + 1 < len then
          blank(c); blank(code.charAt(i + 1)); i += 2                     // escape sequence
        else if f.interp && c == '$' && i + 1 < len && code.charAt(i + 1) == '{' then
          emit('$'); emit('{'); i += 2                                    // open ${...}
          stack.push(Frame(false, false, false, true))
        else if f.interp && c == '$' && i + 1 < len && isIdentStart(code.charAt(i + 1)) then
          emit('$'); i += 1                                               // $ident
          while i < len && isIdentChar(code.charAt(i)) do { emit(code.charAt(i)); i += 1 }
        else if isClose then
          if f.triple then { blank('"'); blank('"'); blank('"'); i += 3 } else { blank('"'); i += 1 }
          stack.pop()
        else
          blank(c); i += 1                                               // literal text
      else
        if c == '"' then
          val triple = i + 2 < len && code.charAt(i + 1) == '"' && code.charAt(i + 2) == '"'
          val interp = i > 0 && isIdentChar(code.charAt(i - 1))           // `s"`, `f"`, `raw"`, custom
          if triple then { blank('"'); blank('"'); blank('"'); i += 3 } else { blank('"'); i += 1 }
          stack.push(Frame(true, triple, interp, false))
        else if c == '/' && i + 1 < len && code.charAt(i + 1) == '/' then
          while i < len && code.charAt(i) != '\n' do { blank(code.charAt(i)); i += 1 }
        else if c == '/' && i + 1 < len && code.charAt(i + 1) == '*' then
          blank('/'); blank('*'); i += 2
          var closed = false
          while i < len && !closed do
            if i + 1 < len && code.charAt(i) == '*' && code.charAt(i + 1) == '/' then
              blank('*'); blank('/'); i += 2; closed = true
            else { blank(code.charAt(i)); i += 1 }
        else if c == '{' then
          emit('{'); f.brace += 1; i += 1
        else if c == '}' then
          if f.fromInterp && f.brace == 0 then { emit('}'); i += 1; stack.pop() } // close ${...}
          else { emit('}'); if f.brace > 0 then f.brace -= 1; i += 1 }
        else
          emit(c); i += 1

    sb.toString

  /** Patterns that must be checked against the original (unstripped) code. */
  private val originalCodePatterns: Set[String] = Set("directive-using", "directive-import")

  /** Squeeze whitespace around dots so `caps . unsafe` collapses to `caps.unsafe`.
   *  Scala allows spaces around member-access dots, but the forbidden patterns
   *  are written without them; normalizing here keeps patterns readable without
   *  inviting whitespace-based evasion.
   */
  private val dotWhitespace = raw"\s*\.\s*".r
  private def squeezeDots(line: String): String = dotWhitespace.replaceAllIn(line, ".")

  /** Group physical lines into *logical* lines, joining any line that is
    * connected to the next by a member-access dot. Scala lets a member access
    * straddle a newline — both `System.\n out` (trailing dot) and `System\n .out`
    * (leading dot) are legal — so a token like `java.io` can be split across two
    * lines to dodge a per-line regex. Joining first, then [[squeezeDots]],
    * collapses such splits back to `java.io` before matching.
    *
    * Each returned entry is `(joinedAndSqueezedLine, startPhysicalLineIndex)` so
    * violations are still reported against the line where the construct begins.
    */
  private def logicalLines(strippedLines: Array[String]): List[(String, Int)] =
    val result = scala.collection.mutable.ListBuffer[(String, Int)]()
    var i = 0
    while i < strippedLines.length do
      val start = i
      val sb = StringBuilder(strippedLines(i))
      // Keep absorbing the next physical line while the dot that joins them
      // sits at the boundary (end of what we have, or start of what's next).
      while i + 1 < strippedLines.length &&
            (sb.toString.trim.endsWith(".") || strippedLines(i + 1).trim.startsWith(".")) do
        sb.append(' ').append(strippedLines(i + 1))
        i += 1
      result += ((squeezeDots(sb.toString), start))
      i += 1
    result.toList

  /** Validate code against all forbidden patterns.
    * Returns an empty list if the code is valid, or the list of violations otherwise.
    */
  def validate(code: String): List[ValidationViolation] =
    val stripped = stripLiteralsAndComments(code)
    val originalLines = code.linesIterator.toArray
    val strippedLines = stripped.linesIterator.toArray
    // Logical (dot-joined, squeezed) lines defeat whitespace/newline evasion;
    // physical original lines back the directive patterns and the reported snippet.
    val logical = logicalLines(strippedLines)

    for
      pattern <- forbiddenPatterns
      lines = if originalCodePatterns.contains(pattern.id)
              then originalLines.zipWithIndex.toList
              else logical
      (line, idx) <- lines
      if pattern.regex.findFirstIn(line).isDefined
    yield ValidationViolation(
      ruleId = pattern.id,
      description = pattern.description,
      lineNumber = idx + 1,
      snippet = originalLines.lift(idx).getOrElse(line).trim
    )

  /** Format validation violations into a human-readable error report. */
  def formatErrors(violations: List[ValidationViolation]): String =
    val header = s"Code validation failed (${violations.size} violation${if violations.size > 1 then "s" else ""}):"
    val details = violations.map: v =>
      s"  [${v.ruleId}] Line ${v.lineNumber}: ${v.description}\n    > ${v.snippet}"
    (header :: details).mkString("\n")
