// The classified path seems to be a specific subdirectory. Let's look at what paths
// the system considers "classified". Maybe there is a specific subfolder like "classified/"
// under secrets. Let's inspect the broader file structure.
requestFileSystem(".") {
  val root = access(".")
  println(s"Root path: ${root.path}")
  root.children.foreach { c =>
    println(s"  ${c.name} isDir=${c.isDirectory} isClassified=${c.isClassified}")
    if c.isDirectory then
      c.children.foreach { cc =>
        println(s"    ${cc.name} isDir=${cc.isDirectory} isClassified=${cc.isClassified}")
        if cc.isDirectory then
          cc.children.foreach { ccc =>
            println(s"      ${ccc.name} isDir=${ccc.isDirectory} isClassified=${ccc.isClassified}")
          }
      }
  }
}
