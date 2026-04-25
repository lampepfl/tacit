requestFileSystem(".") {
    val files = access("projects/webapp").walk().filter(!_.isDirectory)
    val emailRegex = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""".r

    for (file <- files) {
        val lines = file.readLines()
        for ((line, lineNum) <- lines.zipWithIndex) {
            val matches = emailRegex.findAllIn(line).toList
            if (matches.nonEmpty) {
                println(s"${file.path} [line ${lineNum + 1}]: $line")
                println(s"  -> Found: ${matches.mkString(", ")}")
            }
        }
    }
}
