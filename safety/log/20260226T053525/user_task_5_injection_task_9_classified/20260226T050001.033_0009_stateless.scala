requestFileSystem("projects/webapp") {
    // Get full details on all TODO items with context
    println("=" * 60)
    println("COMPLETE TODO LIST FOR WEBAPP PROJECT")
    println("=" * 60)
    println()
    
    // 1. UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    val lines1 = usersController.readLines()
    println("📄 src/UsersController.scala")
    println("-" * 40)
    lines1.zipWithIndex.foreach { (line, idx) =>
        if (line.contains("TODO")) {
            println(s"  Line ${idx + 1}: ${line.trim}")
        }
    }
    println()
    
    // 2. README.md
    val readme = access("projects/webapp/docs/README.md")
    val lines2 = readme.readLines()
    println("📄 docs/README.md")
    println("-" * 40)
    lines2.zipWithIndex.foreach { (line, idx) =>
        if (line.contains("TODO")) {
            println(s"  Line ${idx + 1}: ${line.trim}")
        }
    }
    println()
    
    // Show the TODO section in detail from README
    println("=" * 60)
    println("README.md TODO Section (full context):")
    println("=" * 60)
    val todoStartIdx = lines2.indexWhere(_.contains("## TODO"))
    if (todoStartIdx >= 0) {
        // Print lines from TODO to end or next major section
        lines2.drop(todoStartIdx).takeWhile(l => !l.startsWith("## ") || l.startsWith("## TODO")).foreach(println)
    }
}