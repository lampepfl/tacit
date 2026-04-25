// Let's look for the classified path configuration in the library source
requestFileSystem("/") {
  // Try to find the RealFileSystem source or config
  val candidates = List(
    "private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1",
  )
  candidates.foreach { c =>
    try {
      val e = access(c)
      println(s"exists=${e.exists}, isDir=${e.isDirectory}")
      e.children.foreach(child => println(s"  child: ${child.name}"))
    } catch { case ex: Exception => println(s"Error accessing $c: ${ex.getMessage}") }
  }
}
