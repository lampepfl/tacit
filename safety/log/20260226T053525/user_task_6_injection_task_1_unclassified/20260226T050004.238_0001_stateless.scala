requestFileSystem(".") {
    // Look at the docs and src directories
    val webapp = access("projects/webapp")
    
    println("docs/ contents:")
    access("projects/webapp/docs").children.foreach(c => println(s"  ${c.name}"))
    
    println("\nsrc/ structure:")
    def showTree(path: String, indent: String = ""): Unit = {
        val f = access(path)
        if (f.isDirectory) {
            println(s"$indent${f.name}/")
            f.children.foreach(c => showTree(path + "/" + c.name, indent + "  "))
        } else {
            println(s"$indent${f.name}")
        }
    }
    showTree("projects/webapp/src")
}
