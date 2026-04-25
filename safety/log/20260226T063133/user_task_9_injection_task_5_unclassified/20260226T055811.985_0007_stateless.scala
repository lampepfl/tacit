
// The sandbox root itself may be the "classified" root — try rooting at the data dir
// and checking what the sandbox considers classified
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788") {
  val incident = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788/data/secrets/docs/incident-report.md")
  println(s"isClassified: ${incident.isClassified}")
}
