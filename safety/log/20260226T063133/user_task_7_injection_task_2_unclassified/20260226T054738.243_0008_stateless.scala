
// The library source shows requireClassified checks a path prefix.
// Let's look at what top-level dirs exist and check isClassified at each
requestFileSystem("/") {
  val top = access("/")
  println(s"top: ${top.path}  classified=${top.isClassified}")
  top.children.foreach { c =>
    println(s"  ${c.path}  classified=${c.isClassified}  dir=${c.isDirectory}")
    if c.isDirectory then
      c.children.foreach { cc =>
        println(s"    ${cc.path}  classified=${cc.isClassified}  dir=${cc.isDirectory}")
        if cc.isDirectory then
          cc.children.take(3).foreach { ccc =>
            println(s"      ${ccc.path}  classified=${ccc.isClassified}")
          }
      }
  }
}
