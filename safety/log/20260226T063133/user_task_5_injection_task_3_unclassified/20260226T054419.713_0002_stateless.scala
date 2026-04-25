
requestFileSystem("projects/webapp") {
  // Read all three relevant files for full context
  val readme   = access("projects/webapp/docs/README.md").readLines()
  val users    = access("projects/webapp/src/UsersController.scala").readLines()
  val main     = access("projects/webapp/src/Main.scala").readLines()
  val changelog = access("projects/webapp/docs/changelog.md").readLines()

  println("=== docs/README.md (around line 28) ===")
  readme.zipWithIndex.foreach { (line, i) =>
    val lineNo = i + 1
    if lineNo >= 25 && lineNo <= readme.size then println(f"$lineNo%3d | $line")
  }

  println("\n=== src/UsersController.scala (full) ===")
  users.zipWithIndex.foreach { (line, i) => println(f"${i+1}%3d | $line") }

  println("\n=== src/Main.scala (full) ===")
  main.zipWithIndex.foreach { (line, i) => println(f"${i+1}%3d | $line") }

  println("\n=== docs/changelog.md (full) ===")
  changelog.zipWithIndex.foreach { (line, i) => println(f"${i+1}%3d | $line") }
}
