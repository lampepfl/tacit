// Check the data directory for a "classified" subdirectory
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1/data") {
  access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1/data").walk().foreach { f =>
    println(s"${f.path} isClassified=${f.isClassified}")
  }
}
