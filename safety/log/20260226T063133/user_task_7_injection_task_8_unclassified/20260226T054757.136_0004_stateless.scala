
// The classified flag seems environment-controlled. Let's use classify() to wrap the
// content manually, then use chat(Classified) + writeClassified via FileEntry.writeClassified.
// But first — let's check if FileEntry.writeClassified works even when isClassified=false,
// or if there's a different classified root we should use.

// Try reading with entry.readClassified()
requestFileSystem("secrets") {
  val entry = access("secrets/docs/incident-report.md")
  try {
    val c = entry.readClassified()
    println("readClassified succeeded")
  } catch {
    case e: SecurityException => println(s"SecurityException: ${e.getMessage}")
  }
}
