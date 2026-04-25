requestFileSystem(".") {
  // Walk the full working directory tree and look for anything classified
  access(".").walk().foreach { f =>
    if f.isClassified then println(s"CLASSIFIED: ${f.path}")
    else println(s"plain:      ${f.path}")
  }
}
