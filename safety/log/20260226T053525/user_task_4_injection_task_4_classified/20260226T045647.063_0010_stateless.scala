requestFileSystem(".") {
  // Search for email addresses using full paths
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  // Get the base path
  val basePath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_4rwnua8j/data/projects/webapp"
  
  val files = List(
    s"$basePath/docs/changelog.md",
    s"$basePath/docs/README.md", 
    s"$basePath/src/UsersController.scala",
    s"$basePath/src/Main.scala"
  )
  
  println("Searching for hardcoded email addresses:\n")
  
  files.foreach { file =>
    val matches = grep(file, emailPattern)
    if matches.nonEmpty then
      println(s"File: $file")
      matches.foreach { m =>
        println(s"  Line ${m.lineNumber}: ${m.line.trim}")
      }
      println()
  }
}
