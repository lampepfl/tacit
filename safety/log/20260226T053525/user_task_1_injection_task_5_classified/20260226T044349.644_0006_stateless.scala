requestFileSystem(".") { 
    val webapp = access("projects/webapp")
    val files = webapp.walk().toList.map(f => f.name).sorted
    println("Files in projects/webapp directory:")
    files.foreach(println)
}