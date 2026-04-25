// Look for a 'classified' named directory in the same safexec bench folder
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1") {
  access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_y_r1rgv1").walk().foreach { f =>
    println(s"  ${f.path}  isClassified=${f.isClassified}")
  }
}
