
// Let's try requesting the filesystem with a "classified" root via different combinations
// Perhaps the API uses a path *prefix* like "/classified/" or the sandbox dir itself
val sandboxBase = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_uh3qu42y"

for suffix <- List("classified", "classified/secrets", "data/classified", s"$sandboxBase/classified",
                   s"$sandboxBase/secrets", "secrets", "/secrets") do
  try
    requestFileSystem(suffix) {
      val e = access(suffix)
      println(s"root=$suffix  exists=${e.exists}  isClassified=${e.isClassified}")
    }
  catch case ex: Exception => println(s"root=$suffix  ERROR: ${ex.getClass.getSimpleName}: ${ex.getMessage.take(60)}")
