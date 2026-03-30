package tacit.library

import language.experimental.captureChecking
import caps.assumeSafe
import scala.util.Try

@assumeSafe
private[library] final class ClassifiedImpl[+T](private val value: Try[T]) extends Classified[T]:
  def map[B](op: T -> B): Classified[B] = 
    ClassifiedImpl(value.map(op))
  def flatMap[B](op: T -> Classified[B]): Classified[B] = 
    ClassifiedImpl(value.flatMap(v => ClassifiedImpl.unwrap(op(v))))
  override def toString: String = "Classified(***)"

@assumeSafe
private[library] object ClassifiedImpl:
  def wrap[T](value: -> T): Classified[T] = new ClassifiedImpl(Try(value))
  def unwrap[T](c: Classified[T]): Try[T] = c.asInstanceOf[ClassifiedImpl[T]].value
