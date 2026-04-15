package tacit.library.mcp

object TextParsers:
  /** Convert a Python `repr` string to JSON text.
   *
   *  Python chooses single- or double-quoted string literals to minimise
   *  escaping, so a single dict value can contain a mix of both styles
   *  (e.g. `{"Mom's Home": 'address'}`). This converter tracks the active
   *  delimiter, handles Python escape sequences (`\'`, `\"`, `\n`, ...),
   *  and always emits JSON-compliant double-quoted strings.
   */
  def pythonReprToJson(s: String): String =
    val sb = new StringBuilder
    var i = 0
    while i < s.length do
      val c = s.charAt(i)
      if c == '\'' || c == '"' then
        val quote = c
        sb.append('"')
        i += 1
        while i < s.length && s.charAt(i) != quote do
          val ch = s.charAt(i)
          if ch == '\\' && i + 1 < s.length then
            val next = s.charAt(i + 1)
            next match
              case '\'' =>
                sb.append('\'')
                i += 2
              case '"' =>
                sb.append("\\\"")
                i += 2
              case '\\' =>
                sb.append("\\\\")
                i += 2
              case 'n' =>
                sb.append("\\n")
                i += 2
              case 't' =>
                sb.append("\\t")
                i += 2
              case 'r' =>
                sb.append("\\r")
                i += 2
              case 'b' =>
                sb.append("\\b")
                i += 2
              case 'f' =>
                sb.append("\\f")
                i += 2
              case '/' =>
                sb.append('/')
                i += 2
              case 'u' =>
                sb.append("\\u")
                i += 2
              case 'x' if i + 3 < s.length =>
                sb.append("\\u00").append(s.substring(i + 2, i + 4))
                i += 4
              case other =>
                sb.append(other)
                i += 2
          else if ch == '"' then
            sb.append("\\\"")
            i += 1
          else if ch == '\n' then
            sb.append("\\n")
            i += 1
          else if ch == '\r' then
            sb.append("\\r")
            i += 1
          else if ch == '\t' then
            sb.append("\\t")
            i += 1
          else
            sb.append(ch)
            i += 1
        sb.append('"')
        i += 1
      else if s.startsWith("True", i) && !isIdentContinue(s, i + 4) then
        sb.append("true")
        i += 4
      else if s.startsWith("False", i) && !isIdentContinue(s, i + 5) then
        sb.append("false")
        i += 5
      else if s.startsWith("None", i) && !isIdentContinue(s, i + 4) then
        sb.append("null")
        i += 4
      else
        sb.append(c)
        i += 1
    sb.toString

  private def isIdentContinue(s: String, pos: Int): Boolean =
    pos < s.length && s.charAt(pos).isLetterOrDigit
