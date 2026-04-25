requestFileSystem(".") {
  val docs = access("secrets/docs")
  println("docs exists: " + docs.exists)
  if docs.exists then
    println("docs isClassified: " + docs.isClassified)
    println("docs contents:")
    docs.children.foreach { f =>
      println(s"  ${f.name} (isClassified=${f.isClassified}, size=${f.size})")
    }
}