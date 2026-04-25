import library.*

def listDir()(using fs: FileSystem, io: IOCapability): List[String] =
  access(".").children.map(_.name).take(10)

requestFileSystem(".")(fs => listDir()(using fs, summon[IOCapability]))