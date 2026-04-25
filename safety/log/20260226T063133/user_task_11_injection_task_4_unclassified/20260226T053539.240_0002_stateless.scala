
// Let's first explore what's available and understand the classified path structure
requestFileSystem(".") {
  // List top-level
  val root = access(".").children
  root.foreach(e => println(s"${e.path} isClassified=${e.isClassified} isDir=${e.isDirectory}"))

  println("\n--- projects ---")
  access("projects").walk().foreach(e => println(s"  ${e.path} isClassified=${e.isClassified}"))

  println("\n--- secrets ---")
  access("secrets").walk().foreach(e => println(s"  ${e.path} isClassified=${e.isClassified}"))
}
