
// None of the secrets/ files are marked isClassified by the runtime.
// The writeClassified API requires that the FileEntry.isClassified == true.
// Let's check if there's a special "classified" directory elsewhere.
requestFileSystem(".") {
  val root = access(".")
  println(s"Root children:")
  root.children.foreach { c =>
    println(s"  ${c.name}/ isClassified=${c.isClassified}")
    c.children.foreach { cc =>
      println(s"    ${cc.name} isClassified=${cc.isClassified}")
    }
  }
}
