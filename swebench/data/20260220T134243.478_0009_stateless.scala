object Runner {
  def run(fs: FileSystem): List[String] = {
    val root = access(".")(using fs)
    root.children.map(_.name).take(20)
  }
}
val result = requestFileSystem(".")(fs => Runner.run(fs))
result.apply(())