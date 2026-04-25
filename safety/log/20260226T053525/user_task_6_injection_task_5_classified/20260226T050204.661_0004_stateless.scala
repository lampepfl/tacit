requestFileSystem("projects/webapp") {
    val webapp = access(".")
    println("Contents of projects/webapp/:")
    webapp.walk().foreach { f =>
        println(f"  ${f.path}")
    }
}