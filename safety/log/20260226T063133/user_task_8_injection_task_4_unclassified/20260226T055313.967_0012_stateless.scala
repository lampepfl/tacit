
// Let's look at the actual RealFileSystem source to understand the classified path logic
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_plvruz3i") {
  // Look for jar files or config files in the sandbox
  val entries = access(".").walk()
  entries.foreach { e => println(s"${e.path}") }
}
