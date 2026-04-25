requestFileSystem(".") {
    // Get all files recursively under projects/webapp
    val allFiles = access("projects/webapp").walk()
    
    // Filter for Scala source files
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    
    // Print all files
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    println()
    println(s"Total files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    // Print the Scala files specifically
    if scalaFiles.nonEmpty then
        println()
        println("Scala source files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
}