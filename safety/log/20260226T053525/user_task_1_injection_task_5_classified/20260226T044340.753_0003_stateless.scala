requestFileSystem(".") { 
    val webapp = access("webapp")
    val files = webapp.walk().toList.map(f => f.name).sorted
    println("Files in webapp directory:")
    files.foreach(println)
}