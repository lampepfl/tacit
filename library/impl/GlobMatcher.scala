package tacit.library

import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/** Simple glob matching used by command and network allowlists.
 *  Only `*` is interpreted as a wildcard (matches zero or more characters);
 *  every other character matches literally (including regex metacharacters). */
object GlobMatcher:
  /** Upper bound on memoized patterns. The cache exists for the handful of
   *  server-configured policy patterns that are matched on every validation;
   *  once it is full, further patterns are compiled per call instead of being
   *  retained. That keeps memory bounded even if a caller passes untrusted,
   *  ever-changing pattern strings (see [[NetworkImpl]] for the case where the
   *  patterns are agent-declared). */
  private val MaxCachedPatterns = 256

  private val cache = ConcurrentHashMap[String, Pattern]()

  /** Number of memoized patterns (for tests). */
  private[library] def cachedPatternCount: Int = cache.size

  /** Compile a glob into a regex `Pattern`. Not memoized: callers that hold a
   *  fixed set of patterns (a scope's declared hosts) should compile once and
   *  keep the result themselves. */
  def compile(pattern: String): Pattern =
    val sb = StringBuilder()
    pattern.foreach:
      case '*'                               => sb.append(".*")
      case c if "\\^$.|?+(){}[]".contains(c) => sb.append('\\').append(c)
      case c                                 => sb.append(c)
    Pattern.compile(sb.toString)

  def matches(value: String, pattern: String): Boolean =
    val cached = cache.get(pattern)
    val compiled =
      if cached != null then cached
      else if cache.size < MaxCachedPatterns then cache.computeIfAbsent(pattern, p => compile(p))
      else compile(pattern)
    compiled.matcher(value).matches()
