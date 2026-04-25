
// Let's inspect the RealFileSystem logic more carefully.
// The classified check uses a path prefix. Let's see what path prefix it uses
// by trying a few different root values and checking isClassified.

val roots = List(".", "secrets", "secrets/", "/secrets", "secret")
for root <- roots do
  try
    requestFileSystem(root) {
      val f = access("secrets/docs/incident-report.md")
      println(s"root='$root' path='${f.path}' isClassified=${f.isClassified}")
    }
  catch case e: Exception => println(s"root='$root' ERROR: ${e.getMessage.take(80)}")
