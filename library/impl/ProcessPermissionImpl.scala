package tacit.library

/** Concrete [[ProcessPermission]] capability. Carries the per-scope
 *  `allowedCommands`, the `strictMode` flag (for quick experiments), and the
 *  optional host-level glob allowlist (`commandPermissions`).
 *
 *  These fields live here — not on [[ProcessPermission]] — so the public
 *  capability surface the agent sees stays abstract. */
final class ProcessPermissionImpl private[library] (
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
        // Match on the command's basename: strict mode must block `/bin/cat`,
        // `./cat`, and `cat` alike, otherwise an absolute/relative path trivially
        // evades the unsafe-command denylist. The comparison is case-insensitive
        // (Locale.ROOT): on case-insensitive filesystems (e.g. macOS) `CAT`
        // resolves to `cat` and must not bypass the denylist.
        val slash = math.max(command.lastIndexOf('/'), command.lastIndexOf('\\'))
        val basename = (if slash >= 0 then command.substring(slash + 1).nn else command)
          .toLowerCase(java.util.Locale.ROOT).nn
        if strictMode && ProcessPermissionImpl.unsafeCommands.contains(basename) then
          throw SecurityException(
            s"Strict mode: command '$command' is an unsafe operation. Use requestFileSystem instead."
          )

object ProcessPermissionImpl:
  /** Commands blocked in strict mode, matched case-insensitively on the
   *  command's basename.
   *
   *  This is a convenience denylist for quick experiments, not a security
   *  boundary: it catches the common ways of reading, writing, or shipping
   *  files around the capability API (file utilities, shells, scripting
   *  interpreters, network tools, command runners), but a denylist can never
   *  be complete. Build tools such as `java`, `scala`, `scala-cli`, and
   *  `sbt` are deliberately left available and can run arbitrary code, as
   *  can `find -exec`, `make`, editors with shell escapes, and so on.
   *  Deployments that need a real bound must use `commandPermissions`, which
   *  replaces this list with an explicit allowlist. */
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
    // Shells
    "bash", "sh", "zsh", "fish", "dash",
    // Environment inspection (leaks secrets via process env)
    "env", "printenv",
    // Network tools (exfiltration / unrestricted network access)
    "curl", "wget", "ssh", "sftp", "nc", "netcat", "telnet", "ftp", "socat",
    // Version control (network access + arbitrary file reads via subcommands)
    "git",
    // Crypto/encoding tools (file reads + obfuscation)
    "openssl", "base64",
    // Scripting interpreters (arbitrary code outside the capability API)
    "python", "python3", "perl", "ruby", "node", "php", "lua",
    // Command runners (wrap and re-invoke arbitrary commands)
    "xargs", "nohup", "nice", "timeout", "watch", "parallel", "busybox",
    // Text processors with command-execution features
    "awk", "gawk", "sed",
    // Binary/hex dumpers (read arbitrary files)
    "xxd", "od", "hexdump",
    // Platform escape hatches
    "osascript", "open",
    // Process control
    "kill", "pkill", "killall",
    // Scheduled execution
    "crontab", "at"
  )
