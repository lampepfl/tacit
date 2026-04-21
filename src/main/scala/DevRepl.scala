package tacit

import tacit.core.*
import tacit.executor.*
import Context.*

import org.jline.reader.*
import org.jline.reader.impl.history.DefaultHistory
import org.jline.terminal.{Terminal, TerminalBuilder}

/** TACIT developer REPL — a plain interactive prompt backed by [[ManagedRepl]],
  * useful for iterating on a custom library implementation without running the
  * full MCP server.
  *
  * Uses JLine so arrow keys do line editing (left/right) and history navigation
  * (up/down). History persists across sessions in `~/.tacit_dev_history`.
  *
  * Input is accumulated until a blank line, then submitted. Exit with `:quit`,
  * `:q`, or EOF (Ctrl-D). Ctrl-C cancels the current multi-line input.
  */
@main def StartDevRepl(args: String*): Unit =
  Config.parseCliArgs(args.toArray) match
    case None =>
    case Some(config) => usingContext(config):
      if !config.quiet then printBanner(config)
      val repl = ManagedRepl().init()
      val terminal = TerminalBuilder.builder().system(true).dumb(true).build()
      try readEvalLoop(repl, terminal)
      finally terminal.close()

private def printBanner(config: Config): Unit =
  System.err.println(
    s"""
       | TACIT Library Dev REPL
       | Library:   ${config.libraryJarPath}
       | LibConfig: ${config.libraryConfig.noSpaces}
       |
       | Enter Scala code; submit with a blank line.
       | Commands: :help  :interface  :reset  :quit
       |""".stripMargin)

private def readEvalLoop(initial: ManagedRepl, terminal: Terminal)(using Context): Unit =
  val historyFile = java.nio.file.Paths.get(System.getProperty("user.home"), ".tacit_dev_history")
  val reader = LineReaderBuilder.builder()
    .terminal(terminal)
    .history(new DefaultHistory)
    .variable(LineReader.HISTORY_FILE, historyFile)
    .build()
  val out = terminal.writer()
  val buf = new StringBuilder
  var repl = initial
  var running = true

  while running do
    val prompt = if buf.isEmpty then "tacit> " else "     | "
    try
      val line = reader.readLine(prompt)
      if buf.isEmpty && line.startsWith(":") then
        handleCommand(line.trim, out) match
          case DevCommand.Quit     => running = false
          case DevCommand.Reset    => repl = ManagedRepl().init()
          case DevCommand.Continue => ()
      else if line.isEmpty then
        val code = buf.toString
        buf.clear()
        if code.strip.nonEmpty then
          val result = repl.run(code)
          printResult(result, out)
      else
        buf.append(line).append('\n')
    catch
      case _: EndOfFileException =>
        running = false
      case _: UserInterruptException =>
        // Ctrl-C: discard any pending multi-line input and re-prompt.
        buf.clear()
        out.println()
        out.flush()

  out.println()
  out.flush()

private enum DevCommand:
  case Quit, Reset, Continue

private def handleCommand(cmd: String, out: java.io.PrintWriter): DevCommand = cmd match
  case ":quit" | ":exit" | ":q" =>
    DevCommand.Quit
  case ":reset" =>
    out.println("REPL state reset.")
    out.flush()
    DevCommand.Reset
  case ":help" =>
    out.println(helpText)
    out.flush()
    DevCommand.Continue
  case ":interface" =>
    out.println(loadInterface())
    out.flush()
    DevCommand.Continue
  case other =>
    out.println(s"Unknown command: $other (type :help for commands)")
    out.flush()
    DevCommand.Continue

private def printResult(result: ExecutionResult, out: java.io.PrintWriter): Unit =
  if result.output.nonEmpty then out.println(result.output)
  result.error.foreach: msg =>
    out.println(s"Error: $msg")
  out.flush()

private val helpText: String =
  """|Commands:
     |  :help        Show this help
     |  :interface   Print the library Interface.scala reference
     |  :reset       Discard REPL state and reinitialize
     |  :quit | :q   Exit the REPL (also Ctrl-D)
     |
     |Submit code by entering a blank line. Arrow keys edit the current line
     |and browse history; history persists to ~/.tacit_dev_history.
     |""".stripMargin

private def loadInterface(): String =
  val stream = classOf[ManagedRepl].getResourceAsStream("/Interface.scala")
  if stream == null then "(Interface.scala resource not found on classpath)"
  else
    try scala.io.Source.fromInputStream(stream)(using scala.io.Codec.UTF8).mkString
    finally stream.close()
