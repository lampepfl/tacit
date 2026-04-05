package tacit.agents
package llm.utils

import llm.endpoint.ToolSchema
import tacit.agents.utils.Result
import tacit.agents.utils.Result.ok

import scala.deriving.Mirror
import scala.compiletime.{erasedValue, summonInline, constValue}
import scala.quoted.*

// --- Annotation for field descriptions ---

/** Annotation to attach a description to a case class field for tool schema generation. */
class desc(val value: String) extends scala.annotation.StaticAnnotation

// --- Error type ---

class ToolArgParsingError(val message: String, val cause: Option[Throwable] = None):
  override def toString: String = s"ToolArgParsingError: $message"

object ToolArgParsingError:
  def invalidJson(input: String, cause: Throwable): ToolArgParsingError =
    ToolArgParsingError(s"Invalid JSON: ${cause.getMessage}", Some(cause))

  def missingField(fieldName: String): ToolArgParsingError =
    ToolArgParsingError(s"Missing required field: $fieldName")

  def typeMismatch(fieldName: String, expected: String, got: ujson.Value): ToolArgParsingError =
    ToolArgParsingError(s"Field '$fieldName': expected $expected, got ${got.render()}")

// --- Field-level typeclass ---

trait IsFieldType[T]:
  def schemaType: String
  def items: Option[ToolSchema.Property]
  def parse(v: ujson.Value): Result[T, ToolArgParsingError]

object IsFieldType:
  private def simple[T](tpe: String)(f: ujson.Value => T): IsFieldType[T] =
    new IsFieldType[T]:
      def schemaType = tpe
      def items = None
      def parse(v: ujson.Value): Result[T, ToolArgParsingError] =
        try Right(f(v))
        catch case e: Exception =>
          Left(ToolArgParsingError(s"Expected $tpe, got: ${v.render()}", Some(e)))

  given IsFieldType[String] = simple("string")(_.str)
  given IsFieldType[Int] = simple("integer")(_.num.toInt)
  given IsFieldType[Double] = simple("number")(_.num)
  given IsFieldType[Boolean] = simple("boolean")(_.bool)

  given [T](using inner: IsFieldType[T]): IsFieldType[Option[T]] with
    def schemaType = inner.schemaType
    def items = inner.items
    def parse(v: ujson.Value): Result[Option[T], ToolArgParsingError] =
      v match
        case ujson.Null => Right(None)
        case other => inner.parse(other).map(Some(_))

  given [T](using inner: IsFieldType[T]): IsFieldType[List[T]] with
    def schemaType = "array"
    def items = Some(ToolSchema.Property(`type` = inner.schemaType, items = inner.items))
    def parse(v: ujson.Value): Result[List[T], ToolArgParsingError] =
      try
        val arr = v.arr.toList
        Result:
          arr.map(elem => inner.parse(elem).ok)
      catch case e: Exception =>
        Left(ToolArgParsingError(s"Expected array, got: ${v.render()}", Some(e)))

// --- Main typeclass ---

trait IsToolArg[T]:
  def schema: ToolSchema.Parameters
  def parse(input: String): Result[T, ToolArgParsingError]

object IsToolArg:
  private[utils] class DerivedIsToolArg[T](
    labels: List[String],
    fieldTypes: List[IsFieldType[?]],
    requiredFields: List[String],
    descriptions: List[String],
    parsers: List[(String, ujson.Value) => Result[Any, ToolArgParsingError]],
    mirror: Mirror.ProductOf[T],
  ) extends IsToolArg[T]:
    def schema: ToolSchema.Parameters =
      val properties = labels.zip(fieldTypes).zip(descriptions).map { case ((name, ft), desc) =>
        name -> ToolSchema.Property(`type` = ft.schemaType, description = desc, items = ft.items)
      }
      ToolSchema.Parameters(properties = properties.toMap, required = requiredFields)

    def parse(input: String): Result[T, ToolArgParsingError] =
      val json =
        try ujson.read(input)
        catch case e: Exception =>
          return Left(ToolArgParsingError.invalidJson(input, e))
      val obj =
        try json.obj
        catch case e: Exception =>
          return Left(ToolArgParsingError(s"Expected JSON object, got: ${json.render()}"))
      Result:
        val values = labels.zip(parsers).map: (name, parser) =>
          if obj.contains(name) then
            parser(name, obj(name)).ok
          else
            parser(name, ujson.Null).ok
        val arr = values.toArray(using scala.reflect.ClassTag(classOf[Any]))
        mirror.fromProduct(Tuple.fromArray(arr))

  inline def derived[T](using mirror: Mirror.ProductOf[T]): IsToolArg[T] =
    DerivedIsToolArg[T](
      getFieldLabels[mirror.MirroredElemLabels],
      getFieldTypes[mirror.MirroredElemTypes],
      getRequiredFields[mirror.MirroredElemTypes, mirror.MirroredElemLabels],
      getFieldDescriptions[T],
      getFieldParsers[mirror.MirroredElemTypes],
      mirror,
    )

  /** Extract @desc annotations from case class fields via macro. */
  inline def getFieldDescriptions[T]: List[String] = ${ getFieldDescriptionsImpl[T] }

  private def getFieldDescriptionsImpl[T: Type](using Quotes): Expr[List[String]] =
    import quotes.reflect.*
    val sym = TypeRepr.of[T].typeSymbol
    val ctorParams = sym.primaryConstructor.paramSymss.flatten.filterNot(_.isType)
    val descType = TypeRepr.of[desc]
    // Parse @param tags from class-level docstring as a fallback.
    val paramDocs = parseParamTags(sym.docstring)
    val docs = ctorParams.map: param =>
      // 1. Check @desc annotation first.
      val descAnnot = param.annotations.find(_.tpe <:< descType)
      val description = descAnnot match
        case Some(Apply(_, List(Literal(StringConstant(value))))) => value
        case _ => paramDocs.getOrElse(param.name, "")
      Expr(description)
    Expr.ofList(docs)

  /** Parse `@param name description` tags from a raw Scaladoc comment. */
  private[utils] def parseParamTags(raw: Option[String]): Map[String, String] =
    raw match
      case None => Map.empty
      case Some(s) =>
        // Use a non-greedy match that stops before the next known Scaladoc tag or end of string.
        val scaladocTags = """@(?:param|return|returns|throws|tparam|see|note|example|constructor|deprecated|since|version|todo|inheritdoc|group)\b"""
        val paramPattern = s"""@param\\s+(\\w+)\\s+((?:(?!$scaladocTags).)+)""".r
        val cleaned = s.stripPrefix("/**").stripSuffix("*/")
          .linesIterator
          .map(_.trim.stripPrefix("*").trim)
          .mkString(" ")
        paramPattern.findAllMatchIn(cleaned).map: m =>
          m.group(1).nn -> m.group(2).nn.trim
        .toMap

  // --- inline helpers ---

  private inline def getFieldLabels[T <: Tuple]: List[String] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) => constValue[t].asInstanceOf[String] :: getFieldLabels[ts]

  private inline def getFieldTypes[T <: Tuple]: List[IsFieldType[?]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) => summonInline[IsFieldType[t]] :: getFieldTypes[ts]

  private inline def getFieldParsers[T <: Tuple]: List[(String, ujson.Value) => Result[Any, ToolArgParsingError]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) =>
        val ft = summonInline[IsFieldType[t]]
        val parser: (String, ujson.Value) => Result[Any, ToolArgParsingError] = (name, v) =>
          ft.parse(v) match
            case Right(value) => Right(value)
            case Left(err) if v == ujson.Null => Left(ToolArgParsingError.missingField(name))
            case Left(_) => Left(ToolArgParsingError.typeMismatch(name, ft.schemaType, v))
        parser :: getFieldParsers[ts]

  private inline def getRequiredFields[Types <: Tuple, Labels <: Tuple]: List[String] =
    inline erasedValue[(Types, Labels)] match
      case _: (EmptyTuple, EmptyTuple) => Nil
      case _: ((Option[t] *: ts), (label *: labels)) =>
        getRequiredFields[ts, labels]
      case _: ((t *: ts), (label *: labels)) =>
        constValue[label].asInstanceOf[String] :: getRequiredFields[ts, labels]
