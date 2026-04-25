
// Walk the whole tree to find what IS classified
requestFileSystem(".") {
  val all = access(".").walk()
  all.foreach { f =>
    if f.isClassified then println(s"CLASSIFIED: ${f.path}")
  }
  println("--- also checking api-keys ---")
  val ak = access("secrets/api-keys")
  println(s"api-keys exists: ${ak.exists}, isClassified: ${ak.isClassified}")
  if ak.exists then
    ak.children.foreach { c =>
      println(s"  ${c.path} — isClassified: ${c.isClassified}")
    }
}
