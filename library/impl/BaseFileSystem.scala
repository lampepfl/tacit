package tacit.library

import language.experimental.captureChecking

import java.nio.file.{FileSystems, Path}

/** Shared path-validation and classified-path logic for file-system implementations. */
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
    compiledPatterns.exists(matchesCompiled(_, p))

  /** A classified pattern with its `PathMatcher`s precompiled. Kept as plain
    * data (not `Path => Boolean` closures) so the cached value carries no
    * captured capabilities. */
  private enum CompiledPattern:
    /** No-slash pattern: matches if any single path component matches. */
    case Component(matcher: java.nio.file.PathMatcher)
    /** Absolute pattern: matched against the full absolute path. */
    case Absolute(matchers: List[java.nio.file.PathMatcher])
    /** Relative pattern: matched against the path relative to the root. */
    case Relative(matchers: List[java.nio.file.PathMatcher])

  /** Matchers compiled once from `classifiedPatterns` (constructor-provided
    * and immutable) and reused for every check, instead of recompiling a
    * `PathMatcher` per pattern per file operation. `lazy` so subclass state
    * (`normalizedRoot`) is initialized before first use; thread-safe. */
  private lazy val compiledPatterns: List[CompiledPattern] =
    classifiedPatterns.toList.map(compilePattern)

  private def compilePattern(pattern: String): CompiledPattern =
    val stripped = pattern.stripSuffix("/")
    if stripped.isEmpty then CompiledPattern.Relative(Nil)
    else if !stripped.contains("/") then
      // No slash: match against each path component individually
      CompiledPattern.Component(FileSystems.getDefault.nn.getPathMatcher(s"glob:$stripped"))
    else if Path.of(stripped).isAbsolute then
      // Absolute pattern: resolve non-glob prefix through symlinks, then glob-match
      CompiledPattern.Absolute(globOrDescendantMatchers(resolveGlobPrefix(stripped)))
    else
      // Relative pattern: match against path relative to root
      CompiledPattern.Relative(globOrDescendantMatchers(stripped))

  private def matchesCompiled(compiled: CompiledPattern, p: Path): Boolean =
    compiled match
      case CompiledPattern.Component(matcher) =>
        val count = p.getNameCount
        var i = 0
        var found = false
        while i < count && !found do
          if matcher.matches(p.getName(i)) then found = true
          i += 1
        found
      case CompiledPattern.Absolute(matchers) =>
        matchers.exists(_.matches(p))
      case CompiledPattern.Relative(matchers) =>
        matchers.exists(_.matches(normalizedRoot.relativize(p)))

  /** Precompiled matchers accepting a `glob` exactly or any descendant of a
    * match. For globs starting with `**​/`, leading-`**​/` stripped variants are
    * precompiled too (Java's `**` does not match zero leading directories, so
    * `**​/secrets` must also be tried as `secrets`); expanding them here avoids
    * the per-check retry recursion. */
  private def globOrDescendantMatchers(glob: String): List[java.nio.file.PathMatcher] =
    val fs = FileSystems.getDefault.nn
    def variants(g: String): List[String] =
      if g.startsWith("**/") then g :: variants(g.stripPrefix("**/"))
      else List(g)
    variants(glob).flatMap: g =>
      List(fs.getPathMatcher(s"glob:$g"), fs.getPathMatcher(s"glob:$g/**"))

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

  /** Whether classified writes are permitted through this file system. Set
    * from the server config (`classifiedWrite`, default true) by
    * [[InterfaceImpl]]. Enforced on `FileEntry.writeClassified` so the
    * entry-level path cannot bypass the interface-level gate. */
  protected def classifiedWriteEnabled: Boolean = true

  protected final def requireClassifiedWritable(p: Path, op: String): Unit =
    requireClassified(p, op)
    if !classifiedWriteEnabled then
      throw SecurityException(
        s"Access denied: '$op' is disabled by the server configuration (classifiedWrite = false)"
      )
