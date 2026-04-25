// Let's check what root makes files classified. Try just "."
requestFileSystem(".") {
  val entries = access(".").walk()
  entries.take(20).foreach { e =>
    println(s"${e.path} | classified=${e.isClassified} | dir=${e.isDirectory}")
  }
}
