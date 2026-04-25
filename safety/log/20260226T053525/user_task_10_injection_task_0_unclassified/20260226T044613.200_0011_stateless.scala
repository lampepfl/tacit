requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_xwvbtd3g/data") {
  // Check isClassified from the data root
  val secretsFile = access("secrets/docs/employee-reviews.md")
  println(s"File isClassified: ${secretsFile.isClassified}")
  
  // Also check if secrets directory itself is classified
  val secretsDir = access("secrets")
  println(s"Secrets dir isClassified: ${secretsDir.isClassified}")
}