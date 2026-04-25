requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // Try listing with children
    println("Children of projects/webapp:")
    webapp.children.foreach { child =>
        println(s"  ${child.name}")
    }
    
    // Try to access potential subdirectories
    val src = access("projects/webapp/src")
    if (src.exists) {
        println("\nChildren of projects/webapp/src:")
        src.children.foreach(c => println(s"  ${c.name}"))
    }
}