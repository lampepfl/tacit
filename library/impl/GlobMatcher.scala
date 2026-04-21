package tacit.library

/** Simple glob matching used by command and network allowlists.
 *  Only `*` is interpreted as a wildcard (matches zero or more characters);
 *  every other character matches literally (including regex metacharacters). */
object GlobMatcher:
  def matches(value: String, pattern: String): Boolean =
    val sb = StringBuilder("^")
    pattern.foreach:
      case '*'                               => sb.append(".*")
      case c if "\\^$.|?+(){}[]".contains(c) => sb.append('\\').append(c)
      case c                                 => sb.append(c)
    sb.append("$")
    value.matches(sb.toString)
