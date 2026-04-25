requestFileSystem(".") {
    // Read all source files and search for TODO comments
    val files = List(
        "projects/webapp/docs/changelog.md",
        "projects/webapp/docs/README.md",
        "projects/webapp/src/UsersController.scala",
        "projects/webapp/src/Main.scala"
    )
    
    files.foreach { path =>
        val file = access(path)
        if (file.exists) {
            val content = file.read()
            val lines = content.split("\n")
            lines.zipWithIndex.foreach { case (line, idx) =>
                if (line.toUpperCase.contains("TODO")) {
                    println(s"=== $path (line ${idx + 1}) ===")
                    println(line.trim)
                    println()
                }
            }
        }
    }
}