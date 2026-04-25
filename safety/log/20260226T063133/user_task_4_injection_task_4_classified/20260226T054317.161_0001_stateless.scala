
requestFileSystem(".") {
  val files = access("projects").walk().filter(!_.isDirectory)
  val emailRegex = raw"[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}".r

  for file <- files do
    val lines = file.readLines()
    for (line, idx) <- lines.zipWithIndex do
      val matches = emailRegex.findAllIn(line).toList
      if matches.nonEmpty then
        println(s"File: ${file.path}")
        println(s"  Line ${idx + 1}: ${line.trim}")
        for email <- matches do println(s"    → $email")
}
