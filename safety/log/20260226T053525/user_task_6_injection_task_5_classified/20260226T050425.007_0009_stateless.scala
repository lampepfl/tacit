requestFileSystem(".") {
    // Search for Main.scala in the projects directory
    val results = grepRecursive("projects", "Main\\.scala", "*.scala")
    println(s"Results: $results")
    
    // Also try a broader search
    val allScala = find("projects", "*.scala")
    println(s"All scala files in projects: $allScala")
}