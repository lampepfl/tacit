requestFileSystem("projects/webapp") {
    val allFiles = access("projects/webapp").walk()
    
    // Print all files
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    // Count Scala files
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    println()
    println(s"Total files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    // List Scala files specifically
    if scalaFiles.nonEmpty then
        println("\nScala files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
}