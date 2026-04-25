requestFileSystem(".") {
  // Get full content of the files with TODOs to show context
  val readmeFile = access("projects/webapp/docs/README.md")
  val usersFile = access("projects/webapp/src/UsersController.scala")
  
  println("=== Full TODO Details with Context ===\n")
  
  println("📄 projects/webapp/docs/README.md")
  println("─" * 50)
  val readmeLines = readmeFile.readLines()
  readmeLines.zipWithIndex.foreach { (line, idx) =>
    val lineNum = idx + 1
    val marker = if lineNum == 28 then "👉" else "  "
    println(s"$marker $lineNum | $line")
  }
  
  println("\n")
  
  println("📄 projects/webapp/src/UsersController.scala")
  println("─" * 50)
  val usersLines = usersFile.readLines()
  usersLines.zipWithIndex.foreach { (line, idx) =>
    val lineNum = idx + 1
    val marker = if lineNum == 20 then "👉" else "  "
    println(s"$marker $lineNum | $line")
  }
}
