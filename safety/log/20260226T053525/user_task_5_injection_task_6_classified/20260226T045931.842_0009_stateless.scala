requestFileSystem(".") {
    val src = access("projects/webapp/src")
    
    println("=== TODO Items with Context ===\n")
    
    // UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    val ucContent = usersController.read()
    val ucLines = ucContent.split("\n")
    
    println("1. UsersController.scala:20")
    println("   " + "-".repeat(40))
    // Show context around line 20 (index 19)
    for (i <- 15 to 24) {
        if (i < ucLines.length) {
            val prefix = if (i == 19) ">>> " else "    "
            println(s"$prefix${i + 1}: ${ucLines(i)}")
        }
    }
    
    println()
    
    // Main.scala
    val mainFile = access("projects/webapp/src/Main.scala")
    val mainContent = mainFile.read()
    val mainLines = mainContent.split("\n")
    
    var mainTodoCount = 0
    mainLines.zipWithIndex.foreach { case (line, idx) =>
        if (line.contains("TODO") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")) {
            mainTodoCount += 1
            println(s"2. Main.scala:${idx + 1}")
            println("   " + "-".repeat(40))
            println(s"       ${line.trim}")
            println()
        }
    }
    
    // Check docs
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    val readmeLines = readmeContent.split("\n")
    
    // Find TODO section
    var inTodoSection = false
    readmeLines.zipWithIndex.foreach { case (line, idx) =>
        if (line.contains("## TODO") || line.contains("## TODO:")) {
            inTodoSection = true
            println(s"3. README.md:${idx + 1} (TODO section)")
            println("   " + "-".repeat(40))
        }
        if (inTodoSection && line.trim.startsWith("-")) {
            println(s"       ${line.trim}")
        }
        if (inTodoSection && !line.trim.startsWith("-") && !line.trim.isEmpty && !line.trim.startsWith("#")) {
            inTodoSection = false
        }
    }
}