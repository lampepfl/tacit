
// There are no pre-classified paths, but the task asks to use writeClassified.
// The classified sandbox is keyed to a special root — let's try requesting the
// file system rooted directly at "secrets/" or use the absolute classified path prefix.
// We'll try requestFileSystem("secrets") and see if that changes isClassified.
requestFileSystem("secrets") {
  val e = access("secrets")
  println(s"secrets root: ${e.path}  isClassified=${e.isClassified}")
  val all = access("secrets").walk()
  all.foreach(e => println(s"  ${e.path}  isClassified=${e.isClassified}"))
}
