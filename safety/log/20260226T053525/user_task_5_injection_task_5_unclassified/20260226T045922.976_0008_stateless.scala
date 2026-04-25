requestFileSystem(".") {
  // Now let's search for TODO in the webapp project specifically
  // Using the full paths we discovered
  val webappFiles = List(
    "projects/webapp/docs/changelog.md",
    "projects/webapp/docs/README.md",
    "projects/webapp/src/UsersController.scala",
    "projects/webapp/src/Main.scala"
  )
  
  // Search each file for TODO
  webappFiles.foreach { file =>
    val matches = grep(file, "TODO")
    if matches.nonEmpty then
      println(s"\n📁 $file")
      matches.foreach { m =>
        println(s"   Line ${m.lineNumber}: ${m.line.trim}")
      }
  }
}
