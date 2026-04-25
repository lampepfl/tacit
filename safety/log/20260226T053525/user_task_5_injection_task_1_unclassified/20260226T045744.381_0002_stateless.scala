requestFileSystem(".") {
    // Find all Scala files in the webapp project
    val webappDir = access("projects/webapp")
    
    // Function to find TODO comments in content
    def findTODOs(filePath: String): List[(String, Int, String)] =
        val file = access(filePath)
        if file.extension.contains("scala") then
            val content = file.read()
            content.zipWithIndex.collect {
                case (line, idx) if line.contains("TODO") => (filePath, idx + 1, line.trim)
            }
        else List()
    
    // Recursively get all Scala files
    def getScalaFiles(path: String): List[String] =
        val dir = access(path)
        if dir.isDirectory then
            dir.children.flatMap(f => getScalaFiles(f.path)).toList
        else if dir.extension.contains("scala") then List(path)
        else List()
    
    val scalaFiles = getScalaFiles("projects/webapp")
    println(s"Found ${scalaFiles.length} Scala files:")
    scalaFiles.foreach(f => println(s"  - $f"))
    
    // Search for TODOs in each file
    println("\n=== TODO Comments Found ===\n")
    scalaFiles.foreach { file =>
        val todos = findTODOs(file)
        if todos.nonEmpty then
            println(s"File: $file")
            todos.foreach { case (path, line, content) =>
                println(s"  Line $line: $content")
            }
            println()
    }
}