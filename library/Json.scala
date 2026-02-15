package library.mcpclient

// ─── Minimal JSON ────────────────────────────────────────────────────────────

/** A minimal, dependency-free JSON representation. */
enum Json:
  case JNull
  case JBool(value: Boolean)
  case JNum(value: Double)
  case JStr(value: String)
  case JArr(values: List[Json])
  case JObj(fields: List[(String, Json)])

  def apply(key: String): Json = this match
    case JObj(fields) =>
      fields.find(_._1 == key).map(_._2).getOrElse(JNull)
    case _ => JNull

  def asString: Option[String] = this match
    case JStr(s) => Some(s)
    case _ => None

  def asDouble: Option[Double] = this match
    case JNum(n) => Some(n)
    case _ => None

  def asInt: Option[Int] = asDouble.map(_.toInt)

  def asBool: Option[Boolean] = this match
    case JBool(b) => Some(b)
    case _ => None

  def asArray: Option[List[Json]] = this match
    case JArr(vs) => Some(vs)
    case _ => None

  def asObject: Option[List[(String, Json)]] = this match
    case JObj(fs) => Some(fs)
    case _ => None

  def isNull: Boolean = this == JNull

  def render: String = this match
    case JNull => "null"
    case JBool(b) => b.toString
    case JNum(n) =>
      if n == n.toLong then n.toLong.toString else n.toString
    case JStr(s) => "\"" + escapeJson(s) + "\""
    case JArr(vs) => vs.map(_.render).mkString("[", ",", "]")
    case JObj(fs) =>
      fs.map((k, v) => "\"" + escapeJson(k) + "\":" + v.render).mkString("{", ",", "}")

  private def escapeJson(s: String): String =
    val sb = StringBuilder()
    s.foreach:
      case '"'  => sb.append("\\\"")
      case '\\' => sb.append("\\\\")
      case '\b' => sb.append("\\b")
      case '\f' => sb.append("\\f")
      case '\n' => sb.append("\\n")
      case '\r' => sb.append("\\r")
      case '\t' => sb.append("\\t")
      case c if c < ' ' =>
        sb.append("\\u%04x".format(c.toInt))
      case c => sb.append(c)
    sb.toString

object Json:
  def obj(fields: (String, Json)*): Json = JObj(fields.toList)
  def arr(values: Json*): Json = JArr(values.toList)
  def str(s: String): Json = JStr(s)
  def num(n: Double): Json = JNum(n)
  def bool(b: Boolean): Json = JBool(b)
  val nullValue: Json = JNull

  /** Parse a JSON string. Throws on invalid input. */
  def parse(input: String): Json =
    val p = JsonParser(input)
    val result = p.parseValue()
    p.skipWhitespace()
    if p.pos < input.length then
      throw IllegalArgumentException(s"Unexpected trailing content at position ${p.pos}")
    result

private class JsonParser(input: String):
  var pos: Int = 0

  def skipWhitespace(): Unit =
    while pos < input.length && input(pos).isWhitespace do pos += 1

  def peek: Char =
    if pos >= input.length then
      throw IllegalArgumentException("Unexpected end of JSON input")
    input(pos)

  def advance(): Char =
    val c = peek
    pos += 1
    c

  def expect(c: Char): Unit =
    val got = advance()
    if got != c then
      throw IllegalArgumentException(s"Expected '$c' but got '$got' at position ${pos - 1}")

  def parseValue(): Json =
    skipWhitespace()
    peek match
      case '"' => parseString()
      case '{' => parseObject()
      case '[' => parseArray()
      case 't' | 'f' => parseBool()
      case 'n' => parseNull()
      case c if c == '-' || c.isDigit => parseNumber()
      case c => throw IllegalArgumentException(s"Unexpected character '$c' at position $pos")

  def parseString(): Json.JStr =
    expect('"')
    Json.JStr(readStringContent())

  def readStringContent(): String =
    val sb = StringBuilder()
    while peek != '"' do
      val c = advance()
      if c == '\\' then
        advance() match
          case '"'  => sb.append('"')
          case '\\' => sb.append('\\')
          case '/'  => sb.append('/')
          case 'b'  => sb.append('\b')
          case 'f'  => sb.append('\f')
          case 'n'  => sb.append('\n')
          case 'r'  => sb.append('\r')
          case 't'  => sb.append('\t')
          case 'u'  =>
            val hex = input.substring(pos, pos + 4)
            pos += 4
            sb.append(Integer.parseInt(hex, 16).toChar)
          case e => throw IllegalArgumentException(s"Invalid escape \\$e")
      else sb.append(c)
    expect('"')
    sb.toString

  def parseNumber(): Json.JNum =
    val start = pos
    if peek == '-' then pos += 1
    while pos < input.length && input(pos).isDigit do pos += 1
    if pos < input.length && input(pos) == '.' then
      pos += 1
      while pos < input.length && input(pos).isDigit do pos += 1
    if pos < input.length && (input(pos) == 'e' || input(pos) == 'E') then
      pos += 1
      if pos < input.length && (input(pos) == '+' || input(pos) == '-') then pos += 1
      while pos < input.length && input(pos).isDigit do pos += 1
    Json.JNum(input.substring(start, pos).toDouble)

  def parseBool(): Json.JBool =
    if input.startsWith("true", pos) then
      pos += 4; Json.JBool(true)
    else if input.startsWith("false", pos) then
      pos += 5; Json.JBool(false)
    else throw IllegalArgumentException(s"Invalid boolean at position $pos")

  def parseNull(): Json =
    if input.startsWith("null", pos) then
      pos += 4; Json.JNull
    else throw IllegalArgumentException(s"Invalid null at position $pos")

  def parseObject(): Json.JObj =
    expect('{')
    skipWhitespace()
    if peek == '}' then
      advance()
      return Json.JObj(Nil)
    val fields = scala.collection.mutable.ListBuffer[(String, Json)]()
    while true do
      skipWhitespace()
      expect('"')
      val key = readStringContent()
      skipWhitespace()
      expect(':')
      val value = parseValue()
      fields += ((key, value))
      skipWhitespace()
      if peek == ',' then advance()
      else if peek == '}' then
        advance()
        return Json.JObj(fields.toList)
      else throw IllegalArgumentException(s"Expected ',' or '}}' at position $pos")
    Json.JObj(fields.toList) // unreachable

  def parseArray(): Json.JArr =
    expect('[')
    skipWhitespace()
    if peek == ']' then
      advance()
      return Json.JArr(Nil)
    val values = scala.collection.mutable.ListBuffer[Json]()
    while true do
      values += parseValue()
      skipWhitespace()
      if peek == ',' then advance()
      else if peek == ']' then
        advance()
        return Json.JArr(values.toList)
      else throw IllegalArgumentException(s"Expected ',' or ']' at position $pos")
    Json.JArr(values.toList) // unreachable
