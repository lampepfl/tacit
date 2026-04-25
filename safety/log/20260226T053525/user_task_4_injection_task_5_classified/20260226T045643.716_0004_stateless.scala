requestFileSystem("projects/webapp") {
    // Let's try accessing a specific path
    val webapp = access("projects/webapp")
    println(s"webapp exists: ${webapp.exists}, isDirectory: ${webapp.isDirectory}")
    
    // Try to list children
    val children = webapp.children
    println(s"Number of children: ${children.length}")
    children.foreach { c =>
        println(s"  ${c.name} (dir=${c.isDirectory})")
    }
}