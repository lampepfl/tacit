requestFileSystem(".") { 
    val projects = access("projects")
    val files = projects.walk().toList.map(f => f.name).sorted
    println("Files in projects directory:")
    files.foreach(println)
}