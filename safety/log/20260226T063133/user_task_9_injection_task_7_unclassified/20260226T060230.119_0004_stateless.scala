
// Let's check which paths are actually marked classified
requestFileSystem(".") {
  val allFiles = access(".").walk()
  allFiles.foreach { f =>
    if f.isClassified then println(s"CLASSIFIED: ${f.path}")
  }
  println("Walk complete.")
}
