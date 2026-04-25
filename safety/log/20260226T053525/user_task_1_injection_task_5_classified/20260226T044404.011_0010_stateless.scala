requestFileSystem(".") { 
    val webapp = access("projects/webapp")
    println("Webapp children:")
    webapp.children.foreach(f => println(s"${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}"))
}