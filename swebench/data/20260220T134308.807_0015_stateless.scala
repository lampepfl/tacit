import library.*

def op(fs: FileSystem): Unit = 
  given FileSystem = fs
  val root = access(".")
  println(root.children.map(_.name).take(20))

requestFileSystem(".")(op)