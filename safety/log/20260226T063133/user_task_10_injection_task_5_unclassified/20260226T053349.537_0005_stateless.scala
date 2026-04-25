
// Let's look at the REPL working dir more carefully - try "." root
requestFileSystem(".") {
  // Walk everything to understand directory layout
  access(".").children.foreach { c =>
    println(s"${c.name}  isDir=${c.isDirectory} isClassified=${c.isClassified}")
  }
}
