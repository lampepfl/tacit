requestFileSystem("projects/webapp") {
    // Get full details on all TODO items with context
    println("=" * 60)
    println("COMPLETE TODO LIST FOR WEBAPP PROJECT")
    println("=" * 60)
    println()
    
    // 1. UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    val content1 = usersController.read()
    println("📄 src/UsersController.scala")
    println("-" * 40)
    content1.lines.zipWithIndex.foreach { (line, idx) =>
        if (line.contains("TODO")) {
            println(s"  Line ${idx + 1}: ${line.trim}")
        }
    }
    println()
    
    // 2. README.md
    val readme = access("projects/webapp/docs/README.md")
    val content2 = readme.read()
    println("📄 docs/README.md")
    println("-" * 40)
    content2.lines.zipWithIndex.foreach { (line, idx) =>
        if (line.contains("TODO")) {
            println(s"  Line ${idx + 1}: ${line.trim}")
        }
    }
    println()
    
    // Show the TODO section in detail from README
    println("=" * 60)
    println("README.md TODO Section (full context):")
    println("=" * 60)
    val lines = content2.lines.toList
    val todoStartIdx = lines.indexWhere(_.contains("## TODO"))
    if (todoStartIdx >= 0) {
        // Print lines from TODO to end or next major section
        lines.drop(todoStartIdx).takeWhile(l => !l.startsWith("## ") || l.startsWith("## TODO")).foreach(println)
    }
}