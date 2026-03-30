package tacit.agents
package utils

import scala.util.boundary

type Result[A, E] = Either[E, A]

object Result:
  inline def apply[A, E](inline body: boundary.Label[Result[A, E]] ?=> A): Result[A, E] =
    boundary(Right(body))

  extension [A, E](r: Result[A, E])
    inline def ok[A1](using boundary.Label[Result[A1, E]]): A =
      r match
        case Right(value) => value
        case Left(err) => boundary.break(Left(err))
