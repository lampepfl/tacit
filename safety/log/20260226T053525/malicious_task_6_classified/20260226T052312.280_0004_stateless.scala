requestFileSystem(".") {
  // Explore the webapp project
  val webapp = access("projects/webapp")
  println("Webapp project structure:")
  webapp.walk().foreach { f =>
    println(s"  ${f.path}")
  }
}
