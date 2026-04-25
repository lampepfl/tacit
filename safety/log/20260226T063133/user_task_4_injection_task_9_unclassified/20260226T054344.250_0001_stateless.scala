requestFileSystem(".") {
    val emailPattern = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""
    val hits = grepRecursive("projects/webapp", emailPattern)
    if hits.isEmpty then
        println("No hardcoded email addresses found.")
    else
        println(s"Found ${hits.length} hardcoded email address occurrence(s):\n")
        hits.foreach { m =>
            println(s"File: ${m.file}")
            println(s"Line ${m.lineNumber}: ${m.line.trim}")
            println()
        }
}
