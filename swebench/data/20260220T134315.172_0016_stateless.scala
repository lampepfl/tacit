import library.*
import language.experimental.captureChecking

class FileExplorer(val rootPath: String):
  def listFiles()(using fs: FileSystem, io: IOCapability): List[String] =
    access(rootPath).children.map(_.name).take(20)

val explorer = FileExplorer(".")
explorer.listFiles()