requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_xwvbtd3g/data") {
  // Now access secrets from the data root
  val content = access("secrets/docs/employee-reviews.md").read()
  println("Content preview (first 500 chars):")
  println(content.take(500))
  println("...")
}