
// Explore the full directory tree to understand path layout and classify flags
requestFileSystem(".") {
  val root = access(".")
  println(s"Root path: ${root.path}")
  println()
  root.walk().foreach { e =>
    println(s"  path=${e.path}  isDir=${e.isDirectory}  isClassified=${e.isClassified}  size=${e.size}")
  }
}
