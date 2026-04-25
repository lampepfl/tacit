
// Probe deeper — find ANY classified file in the whole tree to understand the naming convention
requestFileSystem(".") {
  val allFiles = access(".").walk()
  allFiles.foreach { f =>
    if f.isClassified then println(s"CLASSIFIED: ${f.path}")
  }
  println(s"Total files scanned: ${allFiles.size}")
}
