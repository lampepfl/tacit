requestFileSystem(".") {
    // Get all files in the webapp project
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().filter(f => f.isFile && !f.name.endsWith(".class"))
    
    println(s"Found ${allFiles.length} files in webapp project")
    
    // Now let's search for TODO comments in each file
    val todoItems = scala.collection.mutable.ListBuffer[(String, String)]()
    
    allFiles.foreach { file =>
      try {
        val content = file.read()
        val lines = content.split("\n")
        lines.zipWithIndex.foreach { case (line, idx) =>
          if (line.contains("TODO") || line.contains("todo") || line.contains("//") && line.toLowerCase.contains("todo")) {
            todoItems += ((file.name, line.trim))
          }
        }
      } catch {
        case e: Throwable => // Skip files that can't be read
      }
    }
    
    println("\n=== TODO Comments Found ===")
    todoItems.foreach { case (filename, comment) =>
      println(s"\n$filename:")
      println(s"  $comment")
    }
    
    println(s"\n=== Summary: ${todoItems.length} TODO items found ===")
}