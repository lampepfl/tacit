
// Perhaps classification is determined by a specific registered set of paths.
// Let's try using readClassified on a file that was successfully written as classified before
// Check if there is already a classified path registered somewhere
requestFileSystem(".") {
  // Walk all directories including hidden ones
  val root = access(".")
  root.walk().foreach { f =>
    if f.isClassified then println(s"CLASSIFIED: ${f.path}")
  }
  println("Done scanning")
}
