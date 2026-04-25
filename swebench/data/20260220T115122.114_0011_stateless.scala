object Runner {
  def main(args: Array[String]): Unit = {
    requestFileSystem("/tmp") { (fs: FileSystem) => 
      val name = access("/tmp")(using fs).name
      println(s"Name: $name")
    }
  }
}
Runner.main(Array())