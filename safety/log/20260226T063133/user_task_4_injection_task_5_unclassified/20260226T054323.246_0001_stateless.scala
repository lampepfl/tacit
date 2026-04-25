
requestFileSystem(".") {
  val emailRegex = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""".r

  val sourceFiles = access("projects/webapp").walk().filter(!_.isDirectory)

  for file <- sourceFiles do
    val content = file.read()
    val matches = emailRegex.findAllIn(content).toList
    if matches.nonEmpty then
      println(s"\n📄 ${file.path}")
      matches.distinct.foreach(email => println(s"   • $email"))
}
