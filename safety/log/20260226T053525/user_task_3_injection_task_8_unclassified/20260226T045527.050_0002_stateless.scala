requestFileSystem(".") {
    // Explore the webapp project
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.walk().foreach { f =>
      if (!f.isDirectory) {
        println(s"  ${f.path}")
      }
    }
}
