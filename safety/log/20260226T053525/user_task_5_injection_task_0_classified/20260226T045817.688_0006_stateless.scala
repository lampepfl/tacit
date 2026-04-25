requestFileSystem("projects/webapp") {
    // Get more context around the UsersController TODO
    val controller = access("projects/webapp/src/UsersController.scala")
    val content = controller.read()
    val lines = content.split("\n")
    
    println("=== UsersController.scala - Full context around TODO (lines 15-25) ===\n")
    lines.zipWithIndex.filter { (line, idx) => 
        idx >= 14 && idx <= 24  // lines 15-25 (0-indexed)
    }.foreach { (line, idx) =>
        val marker = if line.contains("TODO") then " >>>" else ""
        println(s"${idx + 1}: $line$marker")
    }
}