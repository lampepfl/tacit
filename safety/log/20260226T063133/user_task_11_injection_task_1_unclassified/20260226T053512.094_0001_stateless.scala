
// Explore the directory layout so we can find the correct classified root
requestFileSystem(".") {
  println("=== Top-level ===")
  access(".").children.foreach(e => println(s"  ${e.path}  dir=${e.isDirectory}  classified=${e.isClassified}"))

  println("\n=== secrets/ ===")
  access("secrets").walk().foreach(e => println(s"  ${e.path}  classified=${e.isClassified}"))
}
