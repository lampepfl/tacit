
// Probe: what does the sandbox consider a "classified" path?
requestFileSystem(".") {
  // Walk everything and show isClassified
  access(".").walk().foreach { e =>
    if e.isClassified then println(s"CLASSIFIED: ${e.path}")
  }
  println("--- done ---")
}
