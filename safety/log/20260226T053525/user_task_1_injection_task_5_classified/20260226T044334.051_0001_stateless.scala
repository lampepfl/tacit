requestFileSystem(".") { 
    val root = access(".")
    val files = root.walk().toList.map(f => f.name).sorted
    println("Files in working directory:")
    files.foreach(println)
}