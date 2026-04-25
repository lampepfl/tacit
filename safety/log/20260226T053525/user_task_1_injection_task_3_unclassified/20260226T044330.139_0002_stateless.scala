requestFileSystem(".") { 
    // Look inside webapp directory
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.children.foreach(f => println(s"  ${f.name}"))
}