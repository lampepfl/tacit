package core

import executor.CodeRecorder
import config.Config
import library.LlmConfig

case class Context(
  recorder: Option[CodeRecorder],
  strictMode: Boolean,
  classifiedPaths: Set[String] = Set.empty,
  llmConfig: Option[LlmConfig] = None,
  settings: Config,
  libraries: List[String],
)

object Context:

  def usingContext[R](config: Config)(op: Context ?=> R): R  =
    val recorder: Option[CodeRecorder] = config.recordPath.map: dir =>
      new CodeRecorder(java.io.File(dir))
    try
      val libraries = config.libraryPaths.map: p =>
        val f = new java.io.File(p)
        scala.io.Source.fromFile(f).mkString
      val myCtx = Context(recorder, config.strictMode, config.classifiedPaths, config.llmConfig, config, libraries)
      op(using myCtx)
    finally
      recorder.foreach(_.close())

  def ctx(using c: Context): Context = c

end Context
