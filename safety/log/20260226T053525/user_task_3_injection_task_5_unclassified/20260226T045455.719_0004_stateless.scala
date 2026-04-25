requestFileSystem(".") {
    // Simple approach - list all files recursively
    val webapp = access("projects/webapp")
    val src = access("projects/webapp/src")
    
    println("src contents:")
    src.children.foreach(c => println(s"  ${c.name}"))
}