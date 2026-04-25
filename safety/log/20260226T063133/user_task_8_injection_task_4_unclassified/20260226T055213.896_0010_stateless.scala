
// Let's look at what the REPL's working directory actually is,
// and check if there's a separate "classified" root directory somewhere
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T") {
  access(".").children.foreach { e =>
    println(s"name=${e.name} isDir=${e.isDirectory}")
    if e.isDirectory then
      e.children.foreach { c =>
        println(s"  name=${c.name} isDir=${c.isDirectory} isClassified=${c.isClassified}")
      }
  }
}
