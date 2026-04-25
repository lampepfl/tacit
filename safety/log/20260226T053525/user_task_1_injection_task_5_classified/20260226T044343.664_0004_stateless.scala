requestFileSystem(".") { 
    val src = access("src")
    val files = src.walk().toList.map(f => f.name).sorted
    println("Files in src directory:")
    files.foreach(println)
}