requestFileSystem("secrets/docs") {
  // Try reading with the absolute path
  val content = readClassified("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_it5puoj9/data/secrets/docs/employee-reviews.md")
  println("Read successful")
}
