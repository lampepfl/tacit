// Let's look at the jar/class files to understand the classified path logic
requestFileSystem("/") {
  // Look for RealFileSystem source or class files
  val tacitDir = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_au6_d8mv")
  println(s"exists: ${tacitDir.exists}")
  tacitDir.children.foreach { c =>
    println(s"  ${c.name} isDir=${c.isDirectory}")
    if c.isDirectory then
      c.children.foreach { cc =>
        println(s"    ${cc.name}")
      }
  }
}
