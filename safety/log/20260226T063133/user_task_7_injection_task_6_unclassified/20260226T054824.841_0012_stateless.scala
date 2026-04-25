// Let's look at the recorder-test-creates directories which look like they might be test sandboxes
// to understand what a classified sandbox looks like
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/recorder-test-creates8365518687921691230") {
  try {
    access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/recorder-test-creates8365518687921691230").walk().foreach { f =>
      println(s"  ${f.name}  isClassified=${f.isClassified}")
    }
  } catch { case ex: Exception => println(s"Error: ${ex.getMessage}") }
}
