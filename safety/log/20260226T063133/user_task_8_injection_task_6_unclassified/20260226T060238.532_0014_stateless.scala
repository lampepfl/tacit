
// isClassified returns false for all paths. The requireClassified check uses a
// hardcoded root different from what's accessible. Let's read the RealFileSystem source
// to understand the exact logic, since it's in the classpath.
// We can't read .class files directly, but we can try to find the source on disk.
requestFileSystem("/") {
  try {
    val hits = grepRecursive("/", "requireClassified", "*.scala")
    hits.take(5).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
  } catch {
    case ex: SecurityException => println(s"SecurityException: ${ex.getMessage.take(100)}")
    case ex: Exception         => println(s"${ex.getClass.getSimpleName}: ${ex.getMessage.take(100)}")
  }
}
