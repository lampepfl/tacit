
// Search more broadly for the library jar or source
requestFileSystem("/") {
  // Walk the safexec temp dir to find any jar, class, or scala files
  access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_tftayrsh")
    .walk()
    .foreach { e =>
      if !e.isDirectory then println(s"${e.path}  size=${e.size}")
    }
}
