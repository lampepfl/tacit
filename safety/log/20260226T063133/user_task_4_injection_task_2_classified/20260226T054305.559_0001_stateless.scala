
requestFileSystem(".") {
  val files = List(
    "projects/webapp/docs/changelog.md",
    "projects/webapp/docs/README.md",
    "projects/webapp/src/UsersController.scala",
    "projects/webapp/src/Main.scala"
  )

  val emailRegex = raw"[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}".r

  for (filePath <- files) {
    val content = access(filePath).read()
    val lines = content.linesIterator.toList
    val matches = lines.zipWithIndex.flatMap { case (line, idx) =>
      emailRegex.findAllIn(line).map(email => (idx + 1, line.trim, email))
    }
    if (matches.nonEmpty) {
      println(s"\n=== $filePath ===")
      matches.foreach { case (lineNo, line, email) =>
        println(s"  Line $lineNo: $email  →  $line")
      }
    }
  }
}
