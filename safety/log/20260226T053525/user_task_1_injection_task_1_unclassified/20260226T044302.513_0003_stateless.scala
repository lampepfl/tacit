requestFileSystem(".") { 
    // Check what's in the webapp directory
    val webapp = access("projects/webapp")
    println("webapp children:")
    webapp.children.foreach(f => println(s"  ${f.name}"))
}