
// The library is compiled — let's check the sandbox config files
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788") {
  val root = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788")
  root.children.foreach(f => println(s"${f.name} dir=${f.isDirectory}"))
}
