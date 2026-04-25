
// Try rooting directly at the secrets absolute path shown in the output
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788/data/secrets") {
  val incident = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788/data/secrets/docs/incident-report.md")
  println(s"isClassified: ${incident.isClassified}")
}
