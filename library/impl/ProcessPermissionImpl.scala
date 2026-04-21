package tacit.library

import caps.assumeSafe

/** Concrete [[ProcessPermission]] capability. Carries the per-scope
 *  `allowedCommands`, the `strictMode` flag (for quick experiments), and the
 *  optional host-level glob allowlist (`commandPermissions`).
 *
 *  These fields live here — not on [[ProcessPermission]] — so the public
 *  capability surface the agent sees stays abstract. */
@assumeSafe
final class ProcessPermissionImpl(
  val allowedCommands: Set[String],
  val strictMode: Boolean = false,
  val commandPermissions: Option[Set[String]] = None
) extends ProcessPermission:

  def validateCommand(command: String, args: List[String] = List.empty): Unit =
    if !allowedCommands.contains(command) then
      throw SecurityException(
        s"Access denied: command '$command' is not in allowed commands $allowedCommands"
      )
    commandPermissions match
      case Some(patterns) =>
        val invocation = if args.isEmpty then command else s"$command ${args.mkString(" ")}"
        if !patterns.exists(p => GlobMatcher.matches(invocation, p)) then
          throw SecurityException(
            s"Access denied: invocation '$invocation' does not match any permitted pattern in $patterns"
          )
      case None =>
        if strictMode && ProcessPermissionImpl.unsafeCommands.contains(command) then
          throw SecurityException(
            s"Strict mode: command '$command' is an unsafe operation. Use requestFileSystem instead."
          )

@assumeSafe
object ProcessPermissionImpl:
  /** Commands that perform unsafe operations - blocked in strict mode */
  private val unsafeCommands: Set[String] = Set(
    // Read operations
    "cat", "head", "tail", "less", "more", "tac", "nl",
    // List operations
    "ls", "dir", "find", "locate", "tree", "file", "stat",
    // Write operations
    "touch", "tee", "truncate",
    // Copy/Move operations
    "cp", "mv", "rsync", "scp",
    // Delete operations
    "rm", "rmdir", "unlink", "shred",
    // Directory operations
    "mkdir", "cd", "pwd",
    // Permission operations
    "chmod", "chown", "chgrp",
    // Archive operations
    "tar", "zip", "unzip", "gzip", "gunzip", "bzip2",
    // Link operations
    "ln", "readlink",
    // Disk operations
    "dd", "df", "du",
    // Bash
    "bash", "sh", "zsh", "fish", "dash"
  )
