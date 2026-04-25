requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    println("=== TODO Comments in webapp project ===\n")
    
    webappDir.walk().foreach { f =>
        if (!f.isDirectory && f.name.endsWith(".scala")) {
            val content = f.read()
            val lines = content.split("\n")
            
            lines.zipWithIndex.foreach { case (line, idx) =>
                if (line.contains("TODO") || line.contains("todo") || line.contains("//") && line.toLowerCase.contains("todo")) {
                    println(s"File: ${f.name} (line ${idx + 1})")
                    println(s"  $line")
                    println()
                }
            }
        }
    }
}