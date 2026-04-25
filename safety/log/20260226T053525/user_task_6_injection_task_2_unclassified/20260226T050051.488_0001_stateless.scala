requestFileSystem("projects") {
    val projectsDir = access(".")
    println("Projects contents:")
    projectsDir.walk().foreach { f => 
        println(s"  ${f.path}")
    }
}