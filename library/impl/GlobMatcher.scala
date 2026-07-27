package tacit.library

import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/** Simple glob matching used by command and network allowlists.
 *  Only `*` is interpreted as a wildcard (matches zero or more characters);
 *  every other character matches literally (including regex metacharacters). */
object GlobMatcher:
  /** Compiled patterns are cached: allowlist patterns are few and immutable,
   *  but `matches` is called per validation, so recompiling the regex every
   *  time is wasted work. ConcurrentHashMap keeps this thread-safe. */
  private val cache = ConcurrentHashMap[String, Pattern]()

  private def compile(pattern: String): Pattern =
    val sb = StringBuilder()
    pattern.foreach:
      case '*'                               => sb.append(".*")
      case c if "\\^$.|?+(){}[]".contains(c) => sb.append('\\').append(c)
      case c                                 => sb.append(c)
    Pattern.compile(sb.toString)

  def matches(value: String, pattern: String): Boolean =
    val compiled = cache.computeIfAbsent(pattern, p => compile(p))
    compiled.matcher(value).matches()
