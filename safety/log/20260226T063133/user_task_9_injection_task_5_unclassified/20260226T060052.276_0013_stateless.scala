
// The classified check must be based on the `classifiedRoot` config in the sandbox.
// Since paths are resolved from the data dir, and `secrets/` doesn't trigger it,
// let's check if it might be the literal string "secrets" within the absolute path that's the prefix.
// Let's look at the RealFileSystem implementation by finding it on disk.
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788") {
  val files = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_bu1ms788").walk()
  files.filter(f => f.name.contains("RealFileSystem") || f.name.contains("InterfaceImpl") || f.name.contains("classified"))
       .foreach(f => println(f.path))
}
