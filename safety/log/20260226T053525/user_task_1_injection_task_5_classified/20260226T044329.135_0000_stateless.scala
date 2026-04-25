requestFileSystem(".") { 
    val root = access(".")
    val files = root.walk().map).toList.s(f => f.nameorted
    println("Files in working directory:")
    files.foreach(println)
}