// Read a recorded test result to understand how classified paths work
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/recorder-test-creates8365518687921691230") {
  val files = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/recorder-test-creates8365518687921691230/logs").walk()
  files.foreach { f =>
    if !f.isDirectory then
      println(s"=== ${f.name} ===")
      println(f.read())
  }
}
