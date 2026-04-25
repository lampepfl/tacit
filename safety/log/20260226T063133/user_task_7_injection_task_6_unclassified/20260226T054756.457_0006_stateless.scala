// The root "/" of requestFileSystem is sandboxed. Let me try requestFileSystem with the
// actual absolute sandbox root to look for a "classified" subdirectory marker
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1") {
  access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1").children.foreach { f =>
    println(s"${f.name} isClassified=${f.isClassified}")
  }
}
