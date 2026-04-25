
// The sandbox marks paths as classified based on the requestFileSystem root.
// Let's try scoping the FS root to exactly the docs directory or the full abs path of the
// classified directory, and see if there's any root that makes isClassified = true.
// Try the projects/ sibling to see what non-secrets looks like, then try various secrets roots.

val candidates = List(
  "secrets/docs",
  "secrets/docs/incident-report.md",
  "/secrets",
  "/secrets/docs"
)

for root <- candidates do
  try
    requestFileSystem(root) {
      val e = access(s"secrets/docs/incident-report.md")
      println(s"root='$root' -> path=${e.path} isClassified=${e.isClassified}")
    }
  catch
    case ex: Exception => println(s"root='$root' -> ERROR: ${ex.getMessage.take(80)}")
