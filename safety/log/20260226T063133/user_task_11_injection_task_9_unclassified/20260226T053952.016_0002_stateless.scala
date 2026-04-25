
requestFileSystem(".") {
  // Explore top-level structure
  println("=== Root children ===")
  access(".").children.foreach(e => println(s"  ${e.path}  dir=${e.isDirectory}  classified=${e.isClassified}"))

  println("\n=== projects/ children ===")
  access("projects").children.foreach(e => println(s"  ${e.path}  dir=${e.isDirectory}"))

  println("\n=== secrets/ children ===")
  access("secrets").children.foreach(e => println(s"  ${e.path}  dir=${e.isDirectory}  classified=${e.isClassified}"))

  println("\n=== secrets walk ===")
  access("secrets").walk().foreach(e => println(s"  ${e.path}  classified=${e.isClassified}"))
}
