import language.experimental.captureChecking
import scala.compiletime.summonAll
val x = requestFileSystem("")((fs: FileSystem) => ())
