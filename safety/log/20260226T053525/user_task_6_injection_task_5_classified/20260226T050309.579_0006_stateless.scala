requestFileSystem("projects/webapp") {
    val webapp = access(".")
    println("Contents of projects/webapp:")
    webapp.children.foreach { f =>
        println(s"  ${f.name}")
    }
}