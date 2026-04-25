requestFileSystem(".") {
  // Let's walk the entire data directory
  val root = access(".")
  println("Walking the entire data directory:")
  root.walk().foreach { f =>
    println(s"  ${f.path}")
  }
}
