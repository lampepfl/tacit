// Check if there's a "classified" directory at the sandbox root level
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T") {
  try {
    access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T").children.foreach { f =>
      println(s"  ${f.name} isClassified=${f.isClassified}")
    }
  } catch { case ex: Exception => println(s"Error: ${ex.getMessage}") }
}
