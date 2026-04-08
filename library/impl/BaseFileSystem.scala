package tacit.library

import language.experimental.captureChecking
import caps.assumeSafe

import java.nio.file.{FileSystems, Path}

/** Shared path-validation and classified-path logic for file-system implementations. */
@assumeSafe
abstract class BaseFileSystem extends FileSystem:

  protected def normalizedRoot: Path
  protected def classifiedPatterns: Set[String]
  protected def pathCheck(relativePath: String): Boolean

  protected final def checkPath(resolved: Path): Unit =
    val rel = normalizedRoot.relativize(resolved).toString
    if rel.nonEmpty && !pathCheck(rel) then
      throw SecurityException(s"Access denied: path '$rel' did not pass the check")

  /** Checks if a path matches any classified pattern (gitignore-style).
    *
    * Pattern rules:
    * - No `/` in pattern: matches against any path component.
    *   `.ssh` matches any `.ssh` directory; `.env.*` matches `.env.local` etc.
    * - Absolute pattern (starts with `/`): matched against the full absolute path.
    *   Non-glob prefix is resolved through symlinks. `/tmp/secrets` → `/private/tmp/secrets` on macOS.
    * - Relative pattern with `/`: matched against path relative to the filesystem root.
    *   `secrets/keys` matches `<root>/secrets/keys`; `**​/secrets` matches at any depth.
    * - Glob characters `*`, `?`, `[…]` are supported in all cases.
    *   `*` matches within a single component; `**` matches zero or more directories.
    * - A path is classified if it matches the pattern OR is a descendant of a match.
    * - Trailing `/` is stripped (no directory-only distinction).
    */
  protected final def isClassifiedPath(p: Path): Boolean =
    classifiedPatterns.exists(pattern => matchesClassifiedPattern(p, pattern))

  private def matchesClassifiedPattern(p: Path, pattern: String): Boolean =
    val stripped = pattern.stripSuffix("/")
    if stripped.isEmpty then return false

    if !stripped.contains("/") then
      // No slash: match against each path component individually
      val matcher = FileSystems.getDefault.nn.getPathMatcher(s"glob:$stripped")
      val count = p.getNameCount
      var i = 0
      while i < count do
        if matcher.matches(p.getName(i)) then return true
        i += 1
      false
    else if Path.of(stripped).isAbsolute then
      // Absolute pattern: resolve non-glob prefix through symlinks, then glob-match
      val resolved = resolveGlobPrefix(stripped)
      matchesGlobOrDescendant(p, resolved)
    else
      // Relative pattern: match against path relative to root
      val rel = normalizedRoot.relativize(p)
      matchesGlobOrDescendant(rel, stripped)

  /** Returns true if `target` matches `glob` exactly or is a descendant of a match.
    * For patterns starting with `**​/`, also tries matching without the prefix
    * so that e.g. `**​/secrets` matches a bare `secrets` at the root level.
    */
  private def matchesGlobOrDescendant(target: Path, glob: String): Boolean =
    val fs = FileSystems.getDefault.nn
    if fs.getPathMatcher(s"glob:$glob").matches(target) then return true
    if fs.getPathMatcher(s"glob:$glob/**").matches(target) then return true
    // Java's ** doesn't match zero directories at the start, so strip leading **/
    // and retry: **/secrets should match bare "secrets"
    if glob.startsWith("**/") then
      val rest = glob.stripPrefix("**/")
      matchesGlobOrDescendant(target, rest)
    else false

  // For absolute glob patterns, resolve the longest non-glob prefix through symlinks.
  // E.g., /tmp/secrets/*/keys → /private/tmp/secrets/*/keys on macOS.
  private def resolveGlobPrefix(pattern: String): String =
    val path = Path.of(pattern)
    val root = path.getRoot
    if root == null then return pattern

    val count = path.getNameCount
    val globChars = "*?["

    // Find first component containing glob characters
    var firstGlob = count
    var i = 0
    while i < count && firstGlob == count do
      if globChars.exists(path.getName(i).toString.contains(_)) then
        firstGlob = i
      i += 1

    if firstGlob == 0 then
      // First component is a glob — can only keep the root
      pattern
    else
      // Resolve the prefix before the first glob component
      val prefix =
        if firstGlob == count then path  // no glob chars at all
        else root.resolve(path.subpath(0, firstGlob))
      val abs = prefix.toAbsolutePath.normalize
      val resolved = if java.nio.file.Files.exists(abs) then abs.toRealPath() else abs
      if firstGlob == count then resolved.toString
      else resolved.resolve(path.subpath(firstGlob, count)).toString

  protected final def requireNotClassified(p: Path, op: String): Unit =
    if isClassifiedPath(p) then
      throw SecurityException(
        s"Access denied: '$op' is not allowed on classified path $p. Use classified operations instead."
      )

  protected final def requireClassified(p: Path, op: String): Unit =
    if !isClassifiedPath(p) then
      throw SecurityException(
        s"Access denied: '$op' is only allowed on classified paths, but $p is not classified."
      )
