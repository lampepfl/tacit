package tacit.library.mcp

import language.experimental.captureChecking

/** Minimal self-contained JSON ADT, parser, and serializer. */
enum JValue:
  case JString(value: String)
  case JNumber(value: Double)
  case JBool(value: Boolean)
  case JNull
  case JArray(items: List[JValue])
  case JObject(fields: List[(String, JValue)])

  def field(name: String): JValue = this match
    case JObject(fields) => fields.find(_._1 == name).map(_._2).getOrElse(JNull)
    case _ => JNull

  def apply(index: Int): JValue = this match
    case JArray(items) => if index >= 0 && index < items.size then items(index) else JNull
    case _ => JNull

  def asString: Option[String] = this match
    case JString(v) => Some(v)
    case _ => None

  def asDouble: Option[Double] = this match
    case JNumber(v) => Some(v)
    case _ => None

  def asInt: Option[Int] = this match
    case JNumber(v) if v == v.toInt => Some(v.toInt)
    case _ => None

  def asBool: Option[Boolean] = this match
    case JBool(v) => Some(v)
    case _ => None

  def asArray: Option[List[JValue]] = this match
    case JArray(items) => Some(items)
    case _ => None

  def asObject: Option[List[(String, JValue)]] = this match
    case JObject(fields) => Some(fields)
    case _ => None

  def merge(other: JValue): JValue = (this, other) match
    case (JObject(a), JObject(b)) =>
      val bKeys = b.map(_._1).toSet
      JObject(a.filterNot(f => bKeys.contains(f._1)) ++ b)
    case (_, b) => b

  def compact: String =
    val sb = new StringBuilder
    JValue.writeCompact(this, sb)
    sb.toString

object JValue:
  import JValue.*

  def str(s: String): JValue = JString(s)
  def num(n: Double): JValue = JNumber(n)
  def num(n: Int): JValue = JNumber(n.toDouble)
  def bool(b: Boolean): JValue = JBool(b)
  val nil: JValue = JNull

  def obj(fields: (String, JValue)*): JValue =
    JObject(fields.toList)

  def objOpt(fields: (String, Option[JValue])*): JValue =
    JObject(fields.collect { case (k, Some(v)) => (k, v) }.toList)

  def arr(items: JValue*): JValue =
    JArray(items.toList)

  /** Serialize to compact JSON. */
  private def writeCompact(v: JValue, sb: StringBuilder): Unit = v match
    case JNull => sb.append("null")
    case JBool(b) => sb.append(if b then "true" else "false")
    case JNumber(n) =>
      if n == n.toLong && !n.isInfinite then sb.append(n.toLong.toString)
      else sb.append(n.toString)
    case JString(s) => writeString(s, sb)
    case JArray(items) =>
      sb.append('[')
      var first = true
      items.foreach { item =>
        if !first then sb.append(',')
        first = false
        writeCompact(item, sb)
      }
      sb.append(']')
    case JObject(fields) =>
      sb.append('{')
      var first = true
      fields.foreach { (k, v) =>
        if !first then sb.append(',')
        first = false
        writeString(k, sb)
        sb.append(':')
        writeCompact(v, sb)
      }
      sb.append('}')

  private def writeString(s: String, sb: StringBuilder): Unit =
    sb.append('"')
    var i = 0
    while i < s.length do
      val c = s.charAt(i)
      c match
        case '"'  => sb.append("\\\"")
        case '\\' => sb.append("\\\\")
        case '\n' => sb.append("\\n")
        case '\r' => sb.append("\\r")
        case '\t' => sb.append("\\t")
        case '\b' => sb.append("\\b")
        case '\f' => sb.append("\\f")
        case _ if c < ' ' =>
          sb.append("\\u")
          sb.append(String.format("%04x", Integer.valueOf(c.toInt)))
        case _ => sb.append(c)
      i += 1
    sb.append('"')

  /** Parse a JSON string into a JValue. Throws MCPError on failure. */
  def parse(input: String): JValue =
    val p = new JsonParser(input)
    val result = p.parseValue()
    p.skipWhitespace()
    if p.pos < input.length then
      throw MCPError(s"Unexpected trailing content at position ${p.pos}")
    result

  private class JsonParser(input: String):
    var pos: Int = 0

    def skipWhitespace(): Unit =
      while pos < input.length && input.charAt(pos).isWhitespace do
        pos += 1

    def peek(): Char =
      skipWhitespace()
      if pos >= input.length then throw MCPError("Unexpected end of input")
      input.charAt(pos)

    def advance(): Char =
      val c = input.charAt(pos)
      pos += 1
      c

    def expect(c: Char): Unit =
      skipWhitespace()
      if pos >= input.length || input.charAt(pos) != c then
        throw MCPError(s"Expected '$c' at position $pos")
      pos += 1

    def parseValue(): JValue =
      skipWhitespace()
      if pos >= input.length then throw MCPError("Unexpected end of input")
      input.charAt(pos) match
        case '"' => parseString()
        case '{' => parseObject()
        case '[' => parseArray()
        case 't' => parseLiteral("true", JBool(true))
        case 'f' => parseLiteral("false", JBool(false))
        case 'n' => parseLiteral("null", JNull)
        case c if c == '-' || c.isDigit => parseNumber()
        case c => throw MCPError(s"Unexpected character '$c' at position $pos")

    def parseString(): JValue = JString(parseStringRaw())

    def parseStringRaw(): String =
      expect('"')
      val sb = new StringBuilder
      while pos < input.length && input.charAt(pos) != '"' do
        val c = advance()
        if c == '\\' then
          if pos >= input.length then throw MCPError("Unexpected end of string escape")
          val esc = advance()
          esc match
            case '"'  => sb.append('"')
            case '\\' => sb.append('\\')
            case '/'  => sb.append('/')
            case 'n'  => sb.append('\n')
            case 'r'  => sb.append('\r')
            case 't'  => sb.append('\t')
            case 'b'  => sb.append('\b')
            case 'f'  => sb.append('\f')
            case 'u'  =>
              if pos + 4 > input.length then throw MCPError("Incomplete unicode escape")
              val hex = input.substring(pos, pos + 4)
              pos += 4
              sb.append(Integer.parseInt(hex, 16).toChar)
            case _ => throw MCPError(s"Unknown escape '\\$esc'")
        else
          sb.append(c)
      if pos >= input.length then throw MCPError("Unterminated string")
      pos += 1 // closing quote
      sb.toString

    def parseNumber(): JValue =
      val start = pos
      if pos < input.length && input.charAt(pos) == '-' then pos += 1
      while pos < input.length && input.charAt(pos).isDigit do pos += 1
      if pos < input.length && input.charAt(pos) == '.' then
        pos += 1
        while pos < input.length && input.charAt(pos).isDigit do pos += 1
      if pos < input.length && (input.charAt(pos) == 'e' || input.charAt(pos) == 'E') then
        pos += 1
        if pos < input.length && (input.charAt(pos) == '+' || input.charAt(pos) == '-') then pos += 1
        while pos < input.length && input.charAt(pos).isDigit do pos += 1
      JNumber(input.substring(start, pos).toDouble)

    def parseObject(): JValue =
      expect('{')
      skipWhitespace()
      if pos < input.length && input.charAt(pos) == '}' then
        pos += 1
        return JObject(Nil)
      val fields = scala.collection.mutable.ListBuffer[(String, JValue)]()
      var continue = true
      while continue do
        skipWhitespace()
        val key = parseStringRaw()
        expect(':')
        val value = parseValue()
        fields += ((key, value))
        skipWhitespace()
        if pos < input.length && input.charAt(pos) == ',' then
          pos += 1
        else
          continue = false
      expect('}')
      JObject(fields.toList)

    def parseArray(): JValue =
      expect('[')
      skipWhitespace()
      if pos < input.length && input.charAt(pos) == ']' then
        pos += 1
        return JArray(Nil)
      val items = scala.collection.mutable.ListBuffer[JValue]()
      var continue = true
      while continue do
        items += parseValue()
        skipWhitespace()
        if pos < input.length && input.charAt(pos) == ',' then
          pos += 1
        else
          continue = false
      expect(']')
      JArray(items.toList)

    def parseLiteral(expected: String, result: JValue): JValue =
      if !input.startsWith(expected, pos) then
        throw MCPError(s"Expected '$expected' at position $pos")
      pos += expected.length
      result
