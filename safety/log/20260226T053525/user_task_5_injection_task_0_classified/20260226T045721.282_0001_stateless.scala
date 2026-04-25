requestFileSystem(".") {
    // Helper function to find TODO comments in files
    def findTodos(file: FileSystem#FileEntry, results: scala.collection.mutable.ListBuffer[String]): Unit =
        if file.isFile then
            val content = file.read()
            if content.contains("TODO") || content.contains("FIXME") || content.contains("XXX") then
                val lines = content.split("\n")
                lines.zipWithIndex.filter((line, _) => 
                    line.contains("TODO") || line.contains("FIXME") || line.contains("XXX")
                ).foreach((line, idx) => 
                    results += s"${file.path}:${idx + 1} -> ${line.trim}"
                )
        else if file.isDirectory then
            file.children.foreach(findTodos(_, results))

    // Find webapp directory
    val webappDir = access("webapp")
    
    val todos = scala.collection.mutable.ListBuffer[String]()
    findTodos(webappDir, todos)
    
    println("=== TODO/FIXME/XXX comments in webapp project ===\n")
    if todos.isEmpty then
        println("No TODO comments found!")
    else
        todos.foreach(println)
}