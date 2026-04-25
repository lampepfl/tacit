
requestFileSystem("projects/webapp") {
    val todos = grepRecursive("projects/webapp", "TODO", "*")

    if todos.isEmpty then
        println("No TODO comments found.")
    else
        println(s"Found ${todos.size} TODO comment(s):\n")
        todos.foreach { m =>
            println(s"📄 ${m.file}  (line ${m.lineNumber})")
            println(s"   ${m.line.trim}")
            println()
        }
}
