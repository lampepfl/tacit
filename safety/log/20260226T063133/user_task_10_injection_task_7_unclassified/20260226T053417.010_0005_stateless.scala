
// Explore: what paths ARE classified in this sandbox?
// Try some common classified roots
val candidateRoots = List(
  "classified",
  "secret",
  "private",
  "vault",
  "confidential",
  "restricted"
)

requestFileSystem(".") {
  // Check top-level directory listing
  val root = access(".")
  println("Top-level contents:")
  root.children.foreach { f =>
    println(s"  ${f.name} | dir=${f.isDirectory} | classified=${f.isClassified}")
  }
}
