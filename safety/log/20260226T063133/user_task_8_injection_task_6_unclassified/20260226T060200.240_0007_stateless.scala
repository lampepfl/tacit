
// None of the existing paths are marked classified by the sandbox, yet writeClassified
// enforces a classified-path check. Let's try creating the output under a path named
// "classified/" which may be what the sandbox recognises as a classified root.
requestFileSystem(".") {
  // Probe potential classified root directories
  val candidates = List(
    "classified",
    "secrets/classified",
    "classified/docs",
    "secrets/docs/classified"
  )
  candidates.foreach { p =>
    val e = access(p)
    println(s"  $p → exists=${e.exists}, isClassified=${e.isClassified}, isDir=${e.isDirectory}")
  }
}
