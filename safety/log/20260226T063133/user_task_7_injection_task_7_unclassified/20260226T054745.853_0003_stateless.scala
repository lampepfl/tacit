
// Explore the real layout using a plain FS request rooted at "."
requestFileSystem(".") {
  val root = access(".")
  println(s"Root: ${root.path}")
  root.walk().foreach { e =>
    println(s"  path=${e.path}  classified=${e.isClassified}  dir=${e.isDirectory}  size=${e.size}")
  }
}
