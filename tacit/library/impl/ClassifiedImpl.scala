package tacit.library

import language.experimental.captureChecking
import scala.util.Try

private[library] final class ClassifiedImpl[+T](private val value: Try[T]) extends Classified[T]:
  def map[B](op: T -> B): Classified[B] =
    ClassifiedImpl(value.map(op))
  def flatMap[B](op: T -> Classified[B]): Classified[B] =
    ClassifiedImpl(value.flatMap(v => ClassifiedImpl.unwrap(op(v))))
  override def toString: String = "Classified(***)"

private[library] object ClassifiedImpl:
  def wrap[T](value: -> T): Classified[T] = ClassifiedImpl(Try(value))
  def unwrap[T](c: Classified[T]): Try[T] = c.asInstanceOf[ClassifiedImpl[T]].value
