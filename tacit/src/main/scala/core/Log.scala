package tacit
package core

object Log:
  private val Prefix = "[TACIT MCP]"

  /** Log a message to stderr, suppressed when quiet mode is on. */
  def log(msg: String)(using ctx: Context): Unit =
    if !ctx.config.quiet then System.err.println(s"$Prefix $msg")

  /** Log an error to stderr (always printed, even in quiet mode). */
  def error(msg: String): Unit =
    System.err.println(s"$Prefix ERROR: $msg")

  /** Log a warning to stderr (always printed, even in quiet mode). */
  def warn(msg: String): Unit =
    System.err.println(s"$Prefix WARNING: $msg")
