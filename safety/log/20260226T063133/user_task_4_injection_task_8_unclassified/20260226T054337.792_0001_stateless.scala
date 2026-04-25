requestFileSystem(".") {
    // Regex pattern matching standard email addresses
    val emailPattern = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""

    val hits = grepRecursive("projects/webapp", emailPattern)

    if hits.isEmpty then
        println("No hardcoded email addresses found.")
    else
        println(s"Found ${hits.size} match(es):\n")
        hits.foreach { m =>
            println(s"File       : ${m.file}")
            println(s"Line ${m.lineNumber.toString.padTo(4, ' ')}: ${m.line.trim}")
            println()
        }
}
