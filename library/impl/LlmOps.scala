package tacit.library

import com.openai.client.OpenAIClient
import com.openai.models.ReasoningEffort
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams
import dotty.tools.repl.eval.{Eval, EvalContext, EvalResult, evalLike}

class LlmOps(config: Option[LlmConfig]):

  private def requireConfig(): LlmConfig =
    config.getOrElse(
      throw RuntimeException(
        "LLM is not configured. Pass --llm-base-url, --llm-api-key, --llm-model or use a config file.")
    )

  private lazy val client: OpenAIClient =
    val cfg = requireConfig()
    OpenAIOkHttpClient.builder()
      .apiKey(cfg.apiKey)
      .baseUrl(cfg.baseUrl)
      .build()

  def chat(message: String): String =
    val cfg = requireConfig()
    val params = ChatCompletionCreateParams.builder()
      .model(cfg.model)
      .addUserMessage(message)
      .reasoningEffort(ReasoningEffort.HIGH)
      .build()
    client.chat().completions().create(params)
      .choices().get(0).message().content().orElse("").nn

  /** The classified overload funnels the call through `map`, so exceptions other
   *  than a missing-config is captured as a `Try.Failure` inside the result.
   *  The failure is only observable when the content is later unwrapped. */
  def chat(message: Classified[String]): Classified[String] =
    requireConfig()
    message.map(chat)

  /** Inline-style LLM agent. Asks the LLM to fill the call-site placeholder
   *  with a Scala expression of the requested type, compiles and runs it
   *  under the live REPL, and retries with the diagnostic text on compile
   *  failure. The synthetic parameters (`bindings`, `expectedType`,
   *  `enclosingSource`) are populated by the eval rewriter at each call
   *  site; direct callers can leave them at their defaults. */
  @evalLike def agent[T](
      prompt: String,
      bindings: Array[Eval.Binding] = Array.empty[Eval.Binding],
      expectedType: String = "",
      enclosingSource: String = "",
      maxAttempts: Int = 3
  ): T =
    requireConfig()

    @annotation.tailrec
    def attempt(n: Int, prevCode: String, prevErrors: List[String]): EvalResult[T] =
      val request = AgentPrompt.build(
        prompt, bindings, expectedType, enclosingSource, prevCode, prevErrors)
      val code = AgentPrompt.stripCodeFences(chat(request)).trim
      val r = Eval.evalSafe[T](code, bindings, expectedType, enclosingSource)
      if r.isSuccess || n >= maxAttempts then r
      else attempt(n + 1, code, r.error.nn.errors.toList)

    attempt(1, "", Nil).get

private object AgentPrompt:

  private val Intro =
    """You are an inline Scala 3 code generator for the live REPL.
      |Output ONLY a Scala expression that fills the placeholder in the user's source.
      |No markdown fences, no commentary.""".stripMargin

  /** The full `Interface.scala` source, bundled as a classpath resource by the
   *  build, so the LLM sees the exact capability API surface available at the
   *  REPL call site. */
  private lazy val InterfaceReference: String =
    val stream = classOf[LlmOps].getResourceAsStream("/tacit/Interface.scala.txt")
    if stream != null then
      try scala.io.Source.fromInputStream(stream)(using scala.io.Codec.UTF8).mkString
      finally stream.close()
    else "(Interface.scala source not found on classpath)"

  private val InterfaceIntro =
    """You may only interact with the system through the capability-scoped API
      |defined below. Do not use Java/Scala standard library APIs (java.io,
      |java.nio, scala.io, sys.process, java.net, etc.) directly. All members
      |of the `Interface` trait are pre-loaded into scope (via `import api.*`),
      |so call them unqualified — e.g. `chat("...")`, `requestFileSystem(...)`.
      |""".stripMargin

  private def interfaceSection: String =
    s"""$InterfaceIntro
       |```scala
       |$InterfaceReference
       |```""".stripMargin

  def build(
      task: String,
      bindings: Array[Eval.Binding],
      expectedType: String,
      enclosingSource: String,
      prevCode: String,
      prevErrors: List[String]
  ): String =
    List(
      Intro,
      interfaceSection,
      typeSection(expectedType),
      contextSection(enclosingSource),
      bindingsSection(bindings),
      s"Task: $task",
      typeReminder(expectedType),
      errorSection(prevCode, prevErrors)
    ).filter(_.nonEmpty).mkString("\n\n")

  private def typeSection(expectedType: String): String =
    if expectedType.nonEmpty then
      s"""REQUIRED type of your expression: $expectedType
         |Your expression MUST type-check at this exact type. Any mismatch
         |will be reported as a compile error and you will be asked to retry.""".stripMargin
    else
      "Type of your expression: not pinned at the call site."

  private def contextSection(enclosingSource: String): String =
    if enclosingSource.isEmpty then ""
    else
      s"""Placeholder marker: ${EvalContext.placeholder}
         |Enclosing source:
         |$enclosingSource""".stripMargin

  private def bindingsSection(bindings: Array[Eval.Binding]): String =
    if bindings.isEmpty then ""
    else s"In-scope bindings: ${bindings.iterator.map(_.name).mkString(", ")}"

  private def typeReminder(expectedType: String): String =
    if expectedType.isEmpty then ""
    else s"Reminder: the expression you produce must have type `$expectedType`."

  private def errorSection(prevCode: String, prevErrors: List[String]): String =
    if prevErrors.isEmpty then ""
    else
      s"""Previous attempt:
         |$prevCode
         |
         |Previous attempt failed with:
         |${prevErrors.mkString("\n")}
         |
         |Either fix the cause and emit a corrected expression, or — if the
         |failure looks unrecoverable — emit code that throws a descriptive
         |exception so the human user sees a clear cause. Output ONLY the
         |expression.""".stripMargin

  /** Strip ``` ... ``` fences around an LLM response if present. */
  def stripCodeFences(s: String): String =
    val t = s.trim
    if t.startsWith("```") then
      t.stripPrefix("```scala").stripPrefix("```").stripPrefix("\n").stripSuffix("```").trim
    else t

end AgentPrompt
