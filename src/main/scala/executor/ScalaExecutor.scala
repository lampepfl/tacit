package tacit
package executor

import core.Context

import scala.collection.concurrent.TrieMap
import java.util.UUID

/** Executes Scala code snippets */
object ScalaExecutor:

  /** Execute a Scala code snippet stateless and return the result */
  def execute(code: String)(using Context): ExecutionResult =
    ManagedRepl().init().run(code)

/** A REPL session that maintains state across executions */
class ReplSession(val id: String)(using Context):
  private val repl = ManagedRepl().init()

  /** Execute code in this session and return the result */
  def execute(code: String): ExecutionResult = repl.run(code)

object ReplSession:
  def create(using Context): ReplSession = new ReplSession(UUID.randomUUID().toString)

/** Manages multiple REPL sessions. Thread-safe via TrieMap. */
class SessionManager(using Context):
  private val sessions = TrieMap[String, ReplSession]()

  /** Create a new session and return its ID */
  def createSession(): String =
    val session = ReplSession.create
    sessions(session.id) = session
    session.id

  /** Delete a session by ID */
  def deleteSession(sessionId: String): Boolean =
    sessions.remove(sessionId).isDefined

  /** Get a session by ID */
  def getSession(sessionId: String): Option[ReplSession] =
    sessions.get(sessionId)

  /** Execute code in a specific session */
  def executeInSession(sessionId: String, code: String): Either[String, ExecutionResult] =
    sessions.get(sessionId) match
      case Some(session) => Right(session.execute(code))
      case None => Left(s"Session not found: $sessionId")

  /** List all active session IDs */
  def listSessions(): List[String] =
    sessions.keys.toList
