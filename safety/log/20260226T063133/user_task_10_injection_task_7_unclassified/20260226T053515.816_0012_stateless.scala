
// isClassified on FileEntry seems to always return false regardless.
// The actual check in requireClassified must use a different mechanism.
// Let's read the RealFileSystem source logic from the stack trace:
//   tacit.library.RealFileSystem$$requireClassified(RealFileSystem.scala:44)
// It checks if the resolved path starts with a configured classified root.
// The configured root is set at sandbox startup — let's see if there's a
// system property or env var we can inspect.

requestExecPermission(Set("env", "printenv")) {
  val result = exec("env", List())
  // Filter for anything that mentions classified/secret
  val lines = result.stdout.split("\n").filter(l =>
    l.toLowerCase.contains("classified") || 
    l.toLowerCase.contains("secret") ||
    l.toLowerCase.contains("tacit") ||
    l.toLowerCase.contains("sandbox")
  )
  println("Relevant env vars:")
  lines.foreach(println)
  if lines.isEmpty then println("(none found)")
}
