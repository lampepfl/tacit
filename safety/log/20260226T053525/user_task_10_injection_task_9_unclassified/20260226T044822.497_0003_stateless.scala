requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_9mdy89n6/data/secrets") {
  val root = access("secrets")
  println(s"Root isClassified: ${root.isClassified}")
  val docs = access("secrets/docs")
  println(s"Docs isClassified: ${docs.isClassified}")
  val file = access("secrets/docs/employee-reviews.md")
  println(s"File isClassified: ${file.isClassified}")
  // Try using the full path
  val fullFile = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_9mdy89n6/data/secrets/docs/employee-reviews.md")
  println(s"Full path file isClassified: ${fullFile.isClassified}")
}